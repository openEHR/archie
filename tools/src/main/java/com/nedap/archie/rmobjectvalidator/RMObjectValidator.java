package com.nedap.archie.rmobjectvalidator;

import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rminfo.RMTypeInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Validates a created reference model.
 * Created by pieter.bos on 15/02/16.
 */
public class RMObjectValidator extends RMObjectValidatingProcessor {

    private APathQueryCache queryCache = new APathQueryCache();
    private ModelInfoLookup lookup = ArchieRMInfoLookup.getInstance();

    private ReflectionConstraintImposer constraintImposer = new ReflectionConstraintImposer(lookup);

    public List<RMObjectValidationMessage> validate(Archetype archetype, Object rmObject) {
        clearMessages();
        //run the default validation steps for the reference model - for example non-null values
        runJavaBeanValidations(rmObject);
        List<RMObjectWithPath> objects = Lists.newArrayList(new RMObjectWithPath(rmObject, ""));
        addAllMessages(runArchetypeValidations(objects, "", archetype.getDefinition()));

        return getMessages();
    }

    private void runJavaBeanValidations(Object rmObject) {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        Validator validator = factory.getValidator();
//        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(rmObject);
//        for(ConstraintViolation<Object> violation:constraintViolations) {
//            //TODO: get the path. Might still be possible with an RMObject!
//            addMessage(new RMObjectValidationMessage(violation.getRootBeanClass().getSimpleName(), violation.getRootBeanClass().getSimpleName(), violation.getMessage()));
//        }
    }

    private List<RMObjectValidationMessage> runArchetypeValidations(List<RMObjectWithPath> rmObjects, String path, CObject cobject) {
        List<RMObjectValidationMessage> result = new ArrayList<>();

        result.addAll(validateOccurrences(rmObjects, path, cobject));

        if (rmObjects.isEmpty()) {
            //if this branch of the archetype tree is null in the reference model, we're done validating
            //this has to be done after validateOccurrences(), or required fields do not get validated
            return result;
        }

        if (cobject instanceof CPrimitiveObject) {
            result = validatePrimitiveObject(rmObjects, path, (CPrimitiveObject) cobject);
        } else {
            result = new ArrayList<>();
            if (cobject instanceof CComplexObject) {
                CComplexObject cComplexObject = (CComplexObject) cobject;
                for (CAttributeTuple tuple : cComplexObject.getAttributeTuples()) {
                    result.addAll(validateTuple(cobject, path, rmObjects, tuple));
                }
            }
            for (RMObjectWithPath objectWithPath : rmObjects) {

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
                    String pathSoFar = stripLastPathSegment(path) + objectWithPath.getPath();
                    Object rmObject = objectWithPath.getObject();
                    List<CAttribute> attributes = new ArrayList<>(cobject.getAttributes());
                    attributes.addAll(getDefaultAttributeConstraints(cobject, attributes));
                    for (CAttribute attribute : attributes) {
                        String rmAttributeName = attribute.getRmAttributeName();

                        RMPathQuery aPathQuery = queryCache.getApathQuery("/" + rmAttributeName);

                        Object attributeValue = aPathQuery.find(ArchieRMInfoLookup.getInstance(), rmObject);

                        List<RMObjectValidationMessage> emptyObservationErrors = isObservationEmpty(attribute, rmAttributeName, attributeValue, pathSoFar, cobject);
                        result.addAll(emptyObservationErrors);

                        if (emptyObservationErrors.isEmpty()) {
                            result.addAll(validateMultiplicity(attribute, pathSoFar + "/" + rmAttributeName, attributeValue));
                            if (attribute.isSingle()) {
                                List<List<RMObjectValidationMessage>> subResults = new ArrayList<>();
                                for (CObject childCObject : attribute.getChildren()) {
                                    String query = "/" + rmAttributeName + "[" + childCObject.getNodeId() + "]";
                                    aPathQuery = queryCache.getApathQuery(query);
                                    List<RMObjectWithPath> childNodes = aPathQuery.findList(ArchieRMInfoLookup.getInstance(), rmObject);
                                    List<RMObjectValidationMessage> subResult = runArchetypeValidations(childNodes, pathSoFar + query, childCObject);
                                    subResults.add(subResult);
                                }
                                //a single attribute with multiple CObjects means you can choose which CObject you use
                                //for example, a data value can be a string or an integer.
                                //in this case, only one of the CObjects will validate to a correct value
                                //so as soon as one is correct, so is the data!
                                boolean cObjectWithoutErrorsFound = subResults.stream().anyMatch((subResult) -> subResult.isEmpty());
                                boolean atLeastOneWithoutWrongTypeFound = subResults.stream().anyMatch(this::hasNoneWithWrongType);

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
                            } else {
                                for (CObject childCObject : attribute.getChildren()) {
                                    String query = "/" + rmAttributeName + "[" + childCObject.getNodeId() + "]";
                                    aPathQuery = queryCache.getApathQuery(query);
                                    List<RMObjectWithPath> childRmObjects = aPathQuery.findList(ArchieRMInfoLookup.getInstance(), rmObject);
                                    result.addAll(runArchetypeValidations(childRmObjects, pathSoFar + query, childCObject));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Check if an observation is empty. This is the case if its event contains an empty data attribute.
     *
     * @param attribute       The attribute that is checked
     * @param rmAttributeName The name of the attribute
     * @param attributeValue  The value of the attribute
     * @param pathSoFar       The path of the attribute
     * @param cobject         The constraints that the attribute is checked against
     * @return
     */
    private List<RMObjectValidationMessage> isObservationEmpty(CAttribute attribute, String rmAttributeName, Object attributeValue, String pathSoFar, CObject cobject) {
        List<RMObjectValidationMessage> result = new ArrayList<>();

        CObject parent = attribute.getParent();
        boolean parentIsEvent = parent != null && parent.getRmTypeName().contains("EVENT");
        boolean attributeIsData = rmAttributeName.equals("data");
        boolean attributeIsEmpty = attributeValue == null;
        boolean attributeShouldNotBeEmpty = attribute.getExistence() != null && !attribute.getExistence().has(0);

        if (parentIsEvent && attributeIsData && attributeIsEmpty && attributeShouldNotBeEmpty) {
            String message = "Observation " + getParentObservationTerm(attribute) + " contains no results";
            result.add(new RMObjectValidationMessage(cobject.getParent().getParent(), pathSoFar, message, RMObjectValidationMessageType.EMPTY_OBSERVATION));
        }
        return result;
    }

    /**
     * Retrieve the terminology name of the observation that an attribute is a part of.
     *
     * @param attribute The attribute for which the observation is retrieved
     * @return The name of the observation
     */
    private String getParentObservationTerm(CAttribute attribute) {
        String result = "";
        CObject parent = attribute.getParent();
        while (result.equals("") && parent != null) {
            CAttribute attributeParent = parent.getParent();
            if (attributeParent != null) {
                parent = attributeParent.getParent();
                if (parent.getRmTypeName().equals("OBSERVATION")) {
                    ArchetypeTerm parentTerm = parent.getTerm();
                    if (parentTerm != null) {
                        result = parentTerm.getText();
                    }
                }
            }
        }
        return result;
    }

    private boolean hasNoneWithWrongType(List<RMObjectValidationMessage> subResult) {
        return subResult.stream().noneMatch((message) -> message.getType() == RMObjectValidationMessageType.WRONG_TYPE);
    }

    private List<CAttribute> getDefaultAttributeConstraints(CObject cobject, List<CAttribute> attributes) {
        List<CAttribute> result = new ArrayList<>();
        HashSet<String> alreadyConstraintedAttributes = new HashSet<>();
        for (CAttribute attribute : attributes) {
            alreadyConstraintedAttributes.add(attribute.getRmAttributeName());
        }
        RMTypeInfo typeInfo = this.lookup.getTypeInfo(cobject.getRmTypeName());
        for (RMAttributeInfo defaultAttribute : typeInfo.getAttributes().values()) {
            if (!defaultAttribute.isComputed()) {
                if (!alreadyConstraintedAttributes.contains(defaultAttribute.getRmName())) {
                    CAttribute attribute = constraintImposer.getDefaultAttribute(cobject.getRmTypeName(), defaultAttribute.getRmName());
                    attribute.setParent(cobject);
                    result.add(attribute);
                }
            }
        }
        return result;
    }


    private String stripLastPathSegment(String path) {
        if (path.equals("/")) {
            return "";
        }
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return path;
        }
        return path.substring(0, lastSlashIndex);
    }

    private Collection<? extends RMObjectValidationMessage> validateTuple(CObject cobject, String pathSoFar, List<RMObjectWithPath> rmObjects, CAttributeTuple tuple) {
        List<RMObjectValidationMessage> result = new ArrayList<>();
        if (rmObjects.size() != 1) {
            String message = RMObjectValidationMessageIds.rm_TUPLE_CONSTRAINT.getMessage(cobject.toString(), rmObjects.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
            return result;
        }
        Object rmObject = rmObjects.get(0).getObject();
        if (!tuple.isValid(lookup, rmObject)) {
            String message = RMObjectValidationMessageIds.rm_TUPLE_MISMATCH.getMessage(tuple.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
        }
        return result;
    }

    private List<RMObjectValidationMessage> validatePrimitiveObject(List<RMObjectWithPath> rmObjects, String pathSoFar, CPrimitiveObject cobject) {
        if (cobject.getSocParent() != null) {
            //validate the tuple, not the primitive object directly
            return Collections.emptyList();
        }
        List<RMObjectValidationMessage> result = new ArrayList<>();
        if (rmObjects.size() != 1) {
            String message = RMObjectValidationMessageIds.rm_PRIMITIVE_CONSTRAINT.getMessage(cobject.toString(), rmObjects.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
            return result;
        }
        Object rmObject = rmObjects.get(0).getObject();
        if (!cobject.isValidValue(lookup, rmObject)) {
            String message = RMObjectValidationMessageIds.rm_INVALID_FOR_CONSTRAINT.getMessage(cobject.toString(), rmObject.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
        }
        return result;
    }

    private List<RMObjectValidationMessage> validateMultiplicity(CAttribute attribute, String pathSoFar, Object attributeValue) {
        if (attributeValue instanceof Collection) {
            Collection collectionValue = (Collection) attributeValue;
            //validate multiplicity
            Cardinality cardinality = attribute.getCardinality();
            if (cardinality != null) {
                if (!cardinality.getInterval().has(collectionValue.size())) {
                    String message = RMObjectValidationMessageIds.rm_CARDINALITY_MISMATCH.getMessage(cardinality.getInterval().toString());
                    return Lists.newArrayList(new RMObjectValidationMessage(attribute, pathSoFar, message));
                }
            }
        } else {
            MultiplicityInterval existence = attribute.getExistence();
            if (existence != null) {
                if (!existence.has(attributeValue == null ? 0 : 1)) {
                    String message = RMObjectValidationMessageIds.rm_EXISTENCE_MISMATCH.getMessage(attribute.getRmAttributeName(), attribute.getParent().getRmTypeName(), existence.toString());
                    return Lists.newArrayList((new RMObjectValidationMessage(attribute, pathSoFar, message, RMObjectValidationMessageType.REQUIRED)));
                }
            }
        }
        return new ArrayList<>();
    }

    private List<RMObjectValidationMessage> validateOccurrences(List<RMObjectWithPath> rmObjects, String pathSoFar, CObject cobject) {
        if (cobject.getOccurrences() != null) {
            MultiplicityInterval occurrences = cobject.getOccurrences();
            if (!occurrences.has(rmObjects.size())) {
                String message = RMObjectValidationMessageIds.rm_OCCURRENCE_MISMATCH.getMessage(rmObjects.size(), occurrences.toString());
                RMObjectValidationMessageType messageType = occurrences.isMandatory() ? RMObjectValidationMessageType.REQUIRED : RMObjectValidationMessageType.DEFAULT;
                return Lists.newArrayList(new RMObjectValidationMessage(cobject, pathSoFar, message, messageType));
            }
        }
        return new ArrayList<>();
    }

}
