package com.nedap.archie.rmobjectvalidator;

import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.rminfo.InvariantMethod;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rminfo.RMTypeInfo;
import com.nedap.archie.rmobjectvalidator.validations.RMMultiplicityValidation;
import com.nedap.archie.rmobjectvalidator.validations.RMOccurrenceValidation;
import com.nedap.archie.rmobjectvalidator.validations.RMPrimitiveObjectValidation;
import com.nedap.archie.rmobjectvalidator.validations.RMTupleValidation;
import org.openehr.utils.message.I18n;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validates a created reference model.
 * Created by pieter.bos on 15/02/16.
 */
public class RMObjectValidator extends RMObjectValidatingProcessor {

    private final MetaModel metaModel;
    private APathQueryCache queryCache = new APathQueryCache();
    private ModelInfoLookup lookup;
    private ReflectionConstraintImposer constraintImposer;
    private boolean validateInvariants = true;

    public RMObjectValidator(ModelInfoLookup lookup) {
        this.lookup = lookup;
        this.metaModel = new MetaModel(lookup, null);
        constraintImposer = new ReflectionConstraintImposer(lookup);
    }

    public static RMObjectValidator disabledInvariantChecks(ModelInfoLookup lookup) {
        RMObjectValidator rmObjectValidator = new RMObjectValidator(lookup);
        rmObjectValidator.validateInvariants = false;

        return rmObjectValidator;
    }

    public List<RMObjectValidationMessage> validate(Archetype archetype, Object rmObject) {
        clearMessages();
        List<RMObjectWithPath> objects = Lists.newArrayList(new RMObjectWithPath(rmObject, ""));
        addAllMessages(runArchetypeValidations(objects, "", archetype.getDefinition()));
        return getMessages();
    }

    public List<RMObjectValidationMessage> validate(Object rmObject) {
        clearMessages();
        List<RMObjectWithPath> objects = Lists.newArrayList(new RMObjectWithPath(rmObject, ""));
        addAllMessages(runArchetypeValidations(objects, "", null));
        return getMessages();
    }

    private List<RMObjectValidationMessage> runArchetypeValidations(List<RMObjectWithPath> rmObjects, String path, CObject cobject) {
        List<RMObjectValidationMessage> result = new ArrayList<>(RMOccurrenceValidation.validate(metaModel, rmObjects, path, cobject));
        if (rmObjects.isEmpty()) {
            //if this branch of the archetype tree is null in the reference model, we're done validating
            //this has to be done after validateOccurrences(), or required fields do not get validated
            return result;
        }
        for (RMObjectWithPath objectWithPath : rmObjects) {
            validateInvariants(objectWithPath);
        }
        if(cobject == null) {
            //add default validations
            for (RMObjectWithPath objectWithPath : rmObjects) {
                validateObjectWithPath(result, cobject, path, objectWithPath);
            }
        }
        else if (cobject instanceof CPrimitiveObject) {
            result.addAll(RMPrimitiveObjectValidation.validate(lookup, rmObjects, path, (CPrimitiveObject) cobject));
        } else {
            if (cobject instanceof CComplexObject) {
                CComplexObject cComplexObject = (CComplexObject) cobject;
                for (CAttributeTuple tuple : cComplexObject.getAttributeTuples()) {
                    result.addAll(RMTupleValidation.validate(lookup, cobject, path, rmObjects, tuple));
                }
            }
            for (RMObjectWithPath objectWithPath : rmObjects) {
                validateObjectWithPath(result, cobject, path, objectWithPath);
            }
        }
        return result;
    }

    private void validateInvariants(RMObjectWithPath objectWithPath) {
        if(!validateInvariants) {
            return;
        }
        Object rmObject = objectWithPath.getObject();
        if(rmObject != null) {
            RMTypeInfo typeInfo = lookup.getTypeInfo(rmObject.getClass());
            if(typeInfo != null) {
                for (InvariantMethod invariantMethod : typeInfo.getInvariants()) {
                    if(!invariantMethod.getAnnotation().ignored()) {
                        try {
                            Boolean result = (Boolean) invariantMethod.getMethod().invoke(rmObject);
                            if (!result) {
                                this.addMessage(null, objectWithPath.getPath(), I18n.t("Invariant {0} failed", invariantMethod.getAnnotation().value()), RMObjectValidationMessageType.INVARIANT_ERROR);
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            this.addMessage(null, objectWithPath.getPath(), I18n.t("Exception {0} invoking invariant {1}: {2}", e.getClass().getSimpleName(), invariantMethod.getAnnotation().value(), e.getMessage()), RMObjectValidationMessageType.EXCEPTION);
                        }
                    }
                }
            }
        }
    }

    private void validateObjectWithPath(List<RMObjectValidationMessage> result, CObject cobject, String path, RMObjectWithPath objectWithPath){
        if(cobject == null) {
            Object rmObject = objectWithPath.getObject();
            if(rmObject != null) {
                RMTypeInfo typeInfo = lookup.getTypeInfo(rmObject.getClass());
                if (typeInfo != null) {
                    List<CAttribute> defaultAttributes = RMObjectValidationUtil.getDefaultAttributeConstraints(typeInfo.getRmName(), Lists.newArrayList(), lookup, constraintImposer);
                    validateCAttributes(result, path, objectWithPath, rmObject, null, defaultAttributes);
                }
            }
        } else {
            Class classInConstraint = this.lookup.getClass(cobject.getRmTypeName());
            if (!classInConstraint.isAssignableFrom(objectWithPath.getObject().getClass())) {
                //not a matching constraint. Cannot validate. add error message and stop validating.
                //If another constraint is present, that one will succeed
                result.add(new RMObjectValidationMessage(
                        cobject,
                        objectWithPath.getPath(),
                        RMObjectValidationMessageIds.rm_INCORRECT_TYPE.getMessage(cobject.getRmTypeName(), objectWithPath.getObject().getClass().getSimpleName()),
                        RMObjectValidationMessageType.WRONG_TYPE)
                );
            } else {
                Object rmObject = objectWithPath.getObject();
                List<CAttribute> attributes = new ArrayList<>(cobject.getAttributes());
                attributes.addAll(RMObjectValidationUtil.getDefaultAttributeConstraints(cobject, attributes, lookup, constraintImposer));
                validateCAttributes(result, path, objectWithPath, rmObject, cobject, attributes);
            }
        }
    }

    private void validateCAttributes(List<RMObjectValidationMessage> result, String path, RMObjectWithPath objectWithPath, Object rmObject, CObject cObject, List<CAttribute> attributes) {
        String pathSoFar = RMObjectValidationUtil.stripLastPathSegment(path) + objectWithPath.getPath();
        for (CAttribute attribute : attributes) {
            validateAttributes(result, attribute, cObject, rmObject, pathSoFar);
        }
    }

    private void validateAttributes(List<RMObjectValidationMessage> result, CAttribute attribute, CObject cobject, Object rmObject, String pathSoFar) {
        String rmAttributeName = attribute.getRmAttributeName();
        RMPathQuery aPathQuery = queryCache.getApathQuery("/" + attribute.getRmAttributeName());
        Object attributeValue = aPathQuery.find(lookup, rmObject);
        List<RMObjectValidationMessage> emptyObservationErrors = isObservationEmpty(attribute, rmAttributeName, attributeValue, pathSoFar, cobject);
        result.addAll(emptyObservationErrors);

        if (emptyObservationErrors.isEmpty()) {

            result.addAll(RMMultiplicityValidation.validate(attribute, pathSoFar + "/" + rmAttributeName, attributeValue));

            if(attribute.getChildren() == null || attribute.getChildren().isEmpty()) {
                //no child CObjects. Cardinality/existence has already been validated. Run default RM validations
                String query = "/" + rmAttributeName;
                aPathQuery = queryCache.getApathQuery(query);
                List<RMObjectWithPath> childRmObjects = aPathQuery.findList(lookup, rmObject);
                result.addAll(runArchetypeValidations(childRmObjects, pathSoFar + query, null));
            }
            else if (attribute.isSingle()) {
                validateSingleAttribute(result, attribute, rmObject, pathSoFar);
            } else {

                for (CObject childCObject : attribute.getChildren()) {
                    String query = "/" + rmAttributeName + "[" + childCObject.getNodeId() + "]";
                    aPathQuery = queryCache.getApathQuery(query);
                    List<RMObjectWithPath> childRmObjects = aPathQuery.findList(lookup, rmObject);
                    result.addAll(runArchetypeValidations(childRmObjects, pathSoFar + query, childCObject));
                    //TODO: find all other child RM Objects that don't match with a given node id (eg unconstraint in archetype) and
                    //run default validations against them!
                }
            }
        }
    }

    private void validateSingleAttribute(List<RMObjectValidationMessage> result, CAttribute attribute, Object rmObject, String pathSoFar) {
        List<List<RMObjectValidationMessage>> subResults = new ArrayList<>();

        for (CObject childCObject : attribute.getChildren()) {
            String query = "/" + attribute.getRmAttributeName() + "[" + childCObject.getNodeId() + "]";
            RMPathQuery aPathQuery = queryCache.getApathQuery(query);
            List<RMObjectWithPath> childNodes = aPathQuery.findList(lookup, rmObject);
            List<RMObjectValidationMessage> subResult = runArchetypeValidations(childNodes, pathSoFar + query, childCObject);
            subResults.add(subResult);
        }
        //a single attribute with multiple CObjects means you can choose which CObject you use
        //for example, a data value can be a string or an integer.
        //in this case, only one of the CObjects will validate to a correct value
        //so as soon as one is correct, so is the data!
        boolean cObjectWithoutErrorsFound = subResults.stream().anyMatch(List::isEmpty);
        boolean atLeastOneWithoutWrongTypeFound = subResults.stream().anyMatch(RMObjectValidationUtil::hasNoneWithWrongType);

        if (!cObjectWithoutErrorsFound) {
            if (atLeastOneWithoutWrongTypeFound) {
                for (List<RMObjectValidationMessage> subResult : subResults) {
                    //at least one has the correct type, we can filter out all others
                    result.addAll(subResult.stream().filter((message) -> message.getType() != RMObjectValidationMessageType.WRONG_TYPE).collect(Collectors.toList()));
                }
            } else {
                for (List<RMObjectValidationMessage> subResult : subResults) {
                    result.addAll(subResult);
                }
            }
        }
    }

    /**
     * Check if an observation is empty. This is the case if its event contains an empty data attribute.
     *
     * @param attribute       The attribute that is checked
     * @param rmAttributeName The name of the attribute
     * @param attributeValue  The value of the attribute
     * @param pathSoFar       The path of the attribute
     * @param cobject         The constraints that the attribute is checked against
     */
    private List<RMObjectValidationMessage> isObservationEmpty(CAttribute attribute, String rmAttributeName, Object attributeValue, String pathSoFar, CObject cobject) {
        List<RMObjectValidationMessage> result = new ArrayList<>();
        CObject parent = attribute.getParent();
        boolean parentIsEvent = parent != null && parent.getRmTypeName().contains("EVENT");
        boolean attributeIsData = rmAttributeName.equals("data");
        boolean attributeIsEmpty = attributeValue == null;
        boolean attributeShouldNotBeEmpty = attribute.getExistence() != null && !attribute.getExistence().has(0);

        if (parentIsEvent && attributeIsData && attributeIsEmpty && attributeShouldNotBeEmpty) {
            String message = "Observation " + RMObjectValidationUtil.getParentObservationTerm(attribute) + " contains no results";
            result.add(new RMObjectValidationMessage(cobject == null ? null : cobject.getParent().getParent(), pathSoFar, message, RMObjectValidationMessageType.EMPTY_OBSERVATION));
        }
        return result;
    }


}
