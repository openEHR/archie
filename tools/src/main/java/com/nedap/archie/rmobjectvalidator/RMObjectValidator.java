package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.flattener.OperationalTemplateProvider;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.rminfo.InvariantMethod;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMTypeInfo;
import org.openehr.utils.message.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validates a created reference model object, both against an Operational Template and against all model constraints.
 * If no archetype is given, validates against the model constraints only.
 * Created by pieter.bos on 15/02/16.
 */
public class RMObjectValidator extends RMObjectValidatingProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RMObjectValidator.class);

    private final MetaModel metaModel;
    private final OperationalTemplateProvider operationalTemplateProvider;
    private final APathQueryCache queryCache;
    private final ModelInfoLookup lookup;
    private final ReflectionConstraintImposer constraintImposer;
    private final ValidationConfiguration validationConfiguration;
    private boolean validateInvariants = true;
    private final RmOccurrenceValidator rmOccurrenceValidator;
    private final RmPrimitiveObjectValidator rmPrimitiveObjectValidator;
    private final RmTupleValidator rmTupleValidator;
    private final RmMultiplicityValidator rmMultiplicityValidator;

    /**
     * Creates an RM Object Validator with the given ModelInfoLook class, and the given OperationalTemplateProvider
     * The ModelInfoLookup is used for model access, and model specific constructions.
     * The OperationalTemplateProvider is used to retrieve other referenced archetypes in case of ArchetypeSlots.
     * @param lookup
     * @param provider
     * @deprecated Use {@link #RMObjectValidator(ModelInfoLookup, OperationalTemplateProvider, ValidationConfiguration)} instead.
     */
    @Deprecated
    public RMObjectValidator(ModelInfoLookup lookup, OperationalTemplateProvider provider) {
        this.lookup = lookup;
        this.metaModel = new MetaModel(lookup, null);
        constraintImposer = new ReflectionConstraintImposer(lookup);
        this.operationalTemplateProvider = provider;

        this.validationConfiguration = null; // Leave this null to indicate that no ValidationConfiguration was provided
        ValidationConfiguration dummyValidationConfiguration = new ValidationConfiguration.Builder()
                .failOnUnknownTerminologyId(com.nedap.archie.ValidationConfiguration.isFailOnUnknownTerminologyId())
                .build();
        ValidationHelper validationHelper = new ValidationHelper(this.lookup, dummyValidationConfiguration);
        rmOccurrenceValidator = new RmOccurrenceValidator();
        rmPrimitiveObjectValidator = new RmPrimitiveObjectValidator(validationHelper);
        rmTupleValidator = new RmTupleValidator(this.lookup, validationHelper, rmPrimitiveObjectValidator);
        rmMultiplicityValidator = new RmMultiplicityValidator();
        this.queryCache = new APathQueryCache();
    }

    /**
     * Creates an RM Object Validator with the given ModelInfoLook class, and the given OperationalTemplateProvider
     * The ModelInfoLookup is used for model access, and model specific constructions.
     * The OperationalTemplateProvider is used to retrieve other referenced archetypes in case of ArchetypeSlots.
     */
    public RMObjectValidator(ModelInfoLookup lookup, OperationalTemplateProvider provider, ValidationConfiguration validationConfiguration) {
        this(lookup, provider, validationConfiguration, new APathQueryCache());
    }

    /**
     * Creates an RM Object Validator with the given ModelInfoLook class, and the given OperationalTemplateProvider
     * The ModelInfoLookup is used for model access, and model specific constructions.
     * The OperationalTemplateProvider is used to retrieve other referenced archetypes in case of ArchetypeSlots.
     */
    public RMObjectValidator(ModelInfoLookup lookup, OperationalTemplateProvider provider, ValidationConfiguration validationConfiguration, APathQueryCache queryCache) {
        this.lookup = lookup;
        this.metaModel = new MetaModel(lookup, null);
        constraintImposer = new ReflectionConstraintImposer(lookup);
        this.operationalTemplateProvider = provider;

        this.validationConfiguration = validationConfiguration;
        ValidationHelper validationHelper = new ValidationHelper(this.lookup, validationConfiguration);
        validateInvariants = validationConfiguration.isValidateInvariants();
        rmOccurrenceValidator = new RmOccurrenceValidator();
        rmPrimitiveObjectValidator = new RmPrimitiveObjectValidator(validationHelper);
        rmTupleValidator = new RmTupleValidator(this.lookup, validationHelper, rmPrimitiveObjectValidator);
        rmMultiplicityValidator = new RmMultiplicityValidator();
        this.queryCache = queryCache;
    }

    /**
     * @deprecated Use {@link ValidationConfiguration.Builder#validateInvariants(boolean)} instead.
     */
    @Deprecated
    public void setRunInvariantChecks(boolean validateInvariants) {
        if(this.validationConfiguration != null) {
            throw new IllegalStateException("validateInvariants is already set via validationConfiguration, cannot set it again via setRunInvariantChecks");
        }
        this.validateInvariants = validateInvariants;
    }

    public List<RMObjectValidationMessage> validate(OperationalTemplate template, Object rmObject) {
        clearMessages();
        List<RMObjectWithPath> objects = Collections.singletonList(new RMObjectWithPath(rmObject, ""));
        runArchetypeValidations(messages, objects, ValidationPath.ROOT, template.getDefinition());
        return getMessages();
    }

    public List<RMObjectValidationMessage> validate(Object rmObject) {
        clearMessages();
        List<RMObjectWithPath> objects = Collections.singletonList(new RMObjectWithPath(rmObject, "/"));
        runArchetypeValidations(messages, objects, ValidationPath.ROOT, null);
        return getMessages();
    }

    private void runArchetypeValidations(ValidationMessages result, List<RMObjectWithPath> rmObjects, ValidationPath path, CObject cobject) {
        rmOccurrenceValidator.validate(result, metaModel, rmObjects, path, cobject);
        if (rmObjects.isEmpty()) {
            //if this branch of the archetype tree is null in the reference model, we're done validating
            //this has to be done after validateOccurrences(), or required fields do not get validated
            return;
        }

        // performance-relevant simple for-loop
        int n = rmObjects.size();
        for (int i = 0; i < n; i++) {
            RMObjectWithPath objectWithPath = rmObjects.get(i);
            validateInvariants(result, objectWithPath, path);
        }
        if(cobject == null) {
            //add default validations
            // performance-relevant simple for-loop
            for (int i = 0; i < n; i++) {
                RMObjectWithPath objectWithPath = rmObjects.get(i);
                validateUnconstrainedObjectWithPath(result, path, objectWithPath);
            }
        }
        else if (cobject instanceof CPrimitiveObject) {
            result.addAll(rmPrimitiveObjectValidator.validate(rmObjects, path, (CPrimitiveObject<?, ?>) cobject));
        } else if (cobject instanceof ArchetypeSlot) {
            validateArchetypeSlot(rmObjects, path, cobject, result);
        } else {
            if (cobject instanceof CComplexObject) {
                CComplexObject cComplexObject = (CComplexObject) cobject;
                for (CAttributeTuple tuple : cComplexObject.getAttributeTuples()) {
                    result.addAll(rmTupleValidator.validate(cobject, path, rmObjects, tuple));
                }
            }
            for (RMObjectWithPath objectWithPath : rmObjects) {
                validateConstrainedObjectWithPath(result, cobject, path, objectWithPath);
            }
        }
    }


    private void validateInvariants(ValidationMessages result, RMObjectWithPath objectWithPath, ValidationPath pathSoFar) {
        if (!validateInvariants) {
            return;
        }
        //pathSoFar ends with an attribute, but objectWithPath contains it, so remove that.
        Object rmObject = objectWithPath.getObject();
        if (rmObject == null) {
            return;
        }
        RMTypeInfo typeInfo = lookup.getTypeInfo(rmObject.getClass());
        if (typeInfo == null) {
            return;
        }
        pathSoFar = pathSoFar.stripLastPathSegment();

        List<InvariantMethod> invariants = typeInfo.getInvariants();
        // performance-relevant simple for-loop
        for (int i = 0, invariantsSize = invariants.size(); i < invariantsSize; i++) {
            InvariantMethod invariantMethod = invariants.get(i);
            if (!invariantMethod.getAnnotation().ignored()) {
                try {
                    boolean passed = (boolean) invariantMethod.getMethod().invoke(rmObject);
                    if (!passed) {
                        result.add(new RMObjectValidationMessage(null, pathSoFar.joinPaths(objectWithPath.getPath()).toString(),
                                I18n.t("Invariant {0} failed on type " + typeInfo.getRmName(), invariantMethod.getAnnotation().value()),
                                RMObjectValidationMessageType.INVARIANT_ERROR));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    result.add(new RMObjectValidationMessage(null, pathSoFar.joinPaths(objectWithPath.getPath()).toString(),
                            I18n.t("Unexpected error validating invariant {0} on {1}",
                                    invariantMethod.getAnnotation().value(),
                                    typeInfo.getRmName()),
                            RMObjectValidationMessageType.EXCEPTION));
                    logger.error("Unexpected error validating invariant {} on {}", invariantMethod.getAnnotation().value(), typeInfo.getRmName(), e);
                }
            }
        }
    }

    private void validateUnconstrainedObjectWithPath(ValidationMessages result, ValidationPath path, RMObjectWithPath objectWithPath) {
        Object rmObject = objectWithPath.getObject();
        String archetypeId = lookup.getArchetypeIdFromArchetypedRmObject(rmObject);
        if (archetypeId != null) {
            validateArchetypedObject(result, null, path, objectWithPath, archetypeId);
        } else {
            validateObjectAttributes(result, null, path, objectWithPath);
        }
    }

    private void validateArchetypeSlot(List<RMObjectWithPath> rmObjects, ValidationPath path, CObject cobject, ValidationMessages result) {
        ArchetypeSlot slot = (ArchetypeSlot) cobject;
        for (RMObjectWithPath objectWithPath : rmObjects) {

            Object object = objectWithPath.getObject();

            String archetypeId =  metaModel.getModelInfoLookup().getArchetypeIdFromArchetypedRmObject(object);

            if(archetypeId != null) {
                if(!AOMUtils.archetypeRefMatchesSlotExpression(archetypeId, slot)) {
                    //invalid archetype id, add message
                    this.addMessage(slot, objectWithPath.getPath(),
                            RMObjectValidationMessageIds.rm_ARCHETYPE_ID_SLOT_MISMATCH.getMessage(archetypeId),
                            RMObjectValidationMessageType.ARCHETYPE_SLOT_ID_MISMATCH);
                }
                //but do continue validation!
                validateArchetypedObject(result, cobject, path, objectWithPath, archetypeId);
            } else {
                this.addMessage(slot, objectWithPath.getPath(),
                        RMObjectValidationMessageIds.rm_SLOT_WITHOUT_ARCHETYPE_ID.getMessage(),
                        RMObjectValidationMessageType.ARCHETYPE_SLOT_ID_MISMATCH);
                //but continue validating the RM Objects, of course
                validateConstrainedObjectWithPath(result, cobject, path, objectWithPath);
            }
        }
    }

    private void validateArchetypedObject(ValidationMessages result, CObject cobject, ValidationPath path, RMObjectWithPath objectWithPath, String archetypeId) {
        OperationalTemplate operationalTemplate = operationalTemplateProvider.getOperationalTemplate(archetypeId);
        if (operationalTemplate != null) {
            //occurrences already validated, so nothing left to validate from the archetyepe root
            //from now on, validate from the root of the found OPT
            CObject newRoot = operationalTemplate.getDefinition();
            validateConstrainedObjectWithPath(result, newRoot, path, objectWithPath);
        } else {
            this.addMessage(cobject, objectWithPath.getPath(),
                    RMObjectValidationMessageIds.rm_ARCHETYPE_NOT_FOUND.getMessage(archetypeId),
                    RMObjectValidationMessageType.ARCHETYPE_NOT_FOUND);
            //but continue validating the RM Objects, of course
            if (cobject != null) {
                validateConstrainedObjectWithPath(result, cobject, path, objectWithPath);
            } else {
                 validateObjectAttributes(result, null, path, objectWithPath);
            }
        }
    }

    private void validateConstrainedObjectWithPath(ValidationMessages result, CObject cobject, ValidationPath path, RMObjectWithPath objectWithPath) {
        Class<?> classInConstraint = this.lookup.getClass(cobject.getRmTypeName());
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
            validateObjectAttributes(result, cobject, path, objectWithPath);
        }
    }

    private void validateObjectAttributes(ValidationMessages result, CObject cobject, ValidationPath path, RMObjectWithPath objectWithPath) {
        Object rmObject = objectWithPath.getObject();
        List<CAttribute> attributes;
        if (cobject == null) {
            RMTypeInfo typeInfo = lookup.getTypeInfo(rmObject.getClass());
            if (typeInfo != null) {
                attributes = RMObjectValidationUtil.getDefaultAttributeConstraints(typeInfo.getRmName(), Collections.emptyList(), lookup, constraintImposer);
            } else {
                return; // Type unknown, nothing to validate
            }
        } else {
            attributes = new ArrayList<>(cobject.getAttributes());
            attributes.addAll(RMObjectValidationUtil.getDefaultAttributeConstraints(cobject, attributes, lookup, constraintImposer));
        }
        validateCAttributes(result, path, objectWithPath, rmObject, cobject, attributes);
    }

    private void validateCAttributes(ValidationMessages result, ValidationPath path, RMObjectWithPath objectWithPath, Object rmObject, CObject cObject, List<CAttribute> attributes) {
        //the path contains an attribute, but is missing the [idx] part. So strip the attribute, and add the attribute plus the [idx] part.
        ValidationPath pathSoFar = path.stripLastPathSegment().joinPaths(objectWithPath.getPath());
        for (CAttribute attribute : attributes) {
            validateAttributes(result, attribute, cObject, rmObject, pathSoFar);
        }
    }

    private void validateAttributes(ValidationMessages result, CAttribute attribute, CObject cobject, Object rmObject, ValidationPath pathSoFar) {
        String rmAttributeName = attribute.getRmAttributeName();
        RMPathQuery aPathQuery = queryCache.getForAttribute(rmAttributeName);
        Object attributeValue = aPathQuery.find(lookup, rmObject);
        RMObjectValidationMessage emptyObservationError = isObservationEmpty(attribute, rmAttributeName, attributeValue, pathSoFar, cobject);
        if (emptyObservationError != null) {
            result.add(emptyObservationError);

        } else {
            ValidationPath attributePath = pathSoFar.add(rmAttributeName);
            rmMultiplicityValidator.validate(result, attribute, attributePath, attributeValue);

            if (attribute.getChildren() == null || attribute.getChildren().isEmpty()) {
                //no child CObjects. Cardinality/existence has already been validated. Run default RM validations
                List<RMObjectWithPath> childRmObjects = aPathQuery.findList(lookup, rmObject);
                runArchetypeValidations(result, childRmObjects, attributePath, null);
            }
            else if (attribute.isSingle()) {
                validateSingleAttribute(result, attribute, rmObject, pathSoFar);
            } else {

                for (CObject childCObject : attribute.getChildren()) {
                    aPathQuery = queryCache.getForAttribute(rmAttributeName, childCObject.getNodeId());
                    List<RMObjectWithPath> childRmObjects = aPathQuery.findList(lookup, rmObject);
                     runArchetypeValidations(result, childRmObjects, pathSoFar.add(rmAttributeName, childCObject), childCObject);
                    //TODO: find all other child RM Objects that don't match with a given node id (eg unconstraint in archetype) and
                    //run default validations against them!
                }
            }
        }
    }

    private void validateSingleAttribute(ValidationMessages result, CAttribute attribute, Object rmObject, ValidationPath pathSoFar) {
        List<List<RMObjectValidationMessage>> subResults = null;

        final ValidationMessages currentMessages = new ValidationMessages();

        for (CObject childCObject : attribute.getChildren()) {
            RMPathQuery aPathQuery = queryCache.getForAttribute(attribute.getRmAttributeName(),  childCObject.getNodeId());
            List<RMObjectWithPath> childNodes = aPathQuery.findList(lookup, rmObject);
            runArchetypeValidations(currentMessages, childNodes, pathSoFar.add( attribute.getRmAttributeName(), childCObject), childCObject);
            if (currentMessages.isEmpty()) {
                //a single attribute with multiple CObjects means you can choose which CObject you use
                //for example, a data value can be a string or an integer.
                //in this case, only one of the CObjects will validate to a correct value
                //so as soon as one is correct, so is the data!
                return;
            } else {
                if (subResults == null) {
                    subResults = new ArrayList<>();
                }
                subResults.add(new ArrayList<>(currentMessages.getMessages()));
                currentMessages.clear();
            }
        }

        if (subResults != null) {
            boolean atLeastOneWithoutWrongTypeFound = subResults.stream().anyMatch(RMObjectValidationUtil::hasNoneWithWrongType);

            for (List<RMObjectValidationMessage> subResult : subResults) {
                //at least one has the correct type, we can filter out all others
                if (atLeastOneWithoutWrongTypeFound) {
                    subResult = subResult.stream().filter(message -> message.getType() != RMObjectValidationMessageType.WRONG_TYPE).collect(Collectors.toList());
                }
                result.addAll(subResult);
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
    private RMObjectValidationMessage isObservationEmpty(CAttribute attribute, String rmAttributeName, Object attributeValue, ValidationPath pathSoFar, CObject cobject) {
        CObject parent = attribute.getParent();
        boolean parentIsEvent = parent != null && parent.getRmTypeName().contains("EVENT");
        boolean attributeIsData = rmAttributeName.equals("data");
        boolean attributeIsEmpty = attributeValue == null;
        boolean attributeShouldNotBeEmpty = attribute.getExistence() != null && !attribute.getExistence().has(0);

        if (parentIsEvent && attributeIsData && attributeIsEmpty && attributeShouldNotBeEmpty) {
            String message = "Observation " + RMObjectValidationUtil.getParentObservationTerm(attribute) + " contains no results";
            return new RMObjectValidationMessage(cobject == null ? null : cobject.getParent().getParent(), pathSoFar.toString(), message, RMObjectValidationMessageType.EMPTY_OBSERVATION);
        }
        return null;
    }

}
