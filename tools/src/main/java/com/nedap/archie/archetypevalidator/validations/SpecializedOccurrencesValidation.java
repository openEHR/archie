package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.ConformanceCheckResult;
import com.nedap.archie.aom.utils.NodeIdUtil;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.utils.message.I18n;

import java.util.List;
import java.util.stream.Collectors;

public class SpecializedOccurrencesValidation extends ValidatingVisitor {

    @Override
    public void validate() {
        //only run these if the archetype is specialized and the parent has been found and flattened
        if(archetype.isSpecialized() && flatParent != null) {
            super.validate();
        }
    }

    @Override
    protected void validate(CObject cObject) {
        if(cObject.getParent() == null) {
            return;
        }
        // validate specialised nodes
        if (checkSpecializedNodeHasMatchingPathInParent(cObject)) {
            String flatPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), flatParent.specializationDepth());
            CObject parentCObject = getCObject(flatParent.itemAtPath(flatPath));
            if (parentCObject == null) {
                return;
            }
            ConformanceCheckResult conformanceCheckResult = childNodesConformToParent(cObject, parentCObject);
            if (!conformanceCheckResult.doesConform()) {
                addMessageWithPath(conformanceCheckResult.getErrorType(), cObject.path(),
                        conformanceCheckResult.getError());
            }
        }
    }

    private CObject getCObject(ArchetypeModelObject archetypeModelObject) {
        if(archetypeModelObject instanceof CAttribute) {
            CAttribute attribute = (CAttribute) archetypeModelObject;
            if(attribute.getChildren().size() == 1) {
                return attribute.getChildren().get(0);
            }//TODO: add a numeric identifier to the getPath() method in CObject so this can be deleted and actually works in all cases!
        } else if(archetypeModelObject instanceof CObject) {
            return (CObject) archetypeModelObject;
        }
        return null;
    }

    /**
     * Complicated method - does validations and returns true if the given node has a matching path in the parent
     * @param cObject
     * @return
     */
    private boolean checkSpecializedNodeHasMatchingPathInParent(CObject cObject) {
        boolean result = false;
        if (cObject.isRootNode() || !cObject.getParent().isSecondOrderConstrained()) {
            if (AOMUtils.getSpecializationDepthFromCode(cObject.getNodeId()) <= flatParent.specializationDepth()
                    || new NodeIdUtil(cObject.getNodeId()).isRedefined()) {
                if (!AOMUtils.isPhantomPathAtLevel(cObject.getPathSegments(), flatParent.specializationDepth())) {
                    String flatPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), flatParent.specializationDepth());
                    CObject parentCObject = getCObject(flatParent.itemAtPath(flatPath));
                    result = parentCObject != null;
                }
            }
        }
        return result;
    }

    private ConformanceCheckResult childNodesConformToParent(CObject childCObject, CObject parentCObject) {
        if (childCObject.getParent() == null) {
            return ConformanceCheckResult.conforms();
        }

        MultiplicityInterval parentNodeOccurrences = parentCObject.effectiveOccurrences(metaModel::referenceModelPropMultiplicity);
        MultiplicityInterval childNodeOccurrences = childCObject.effectiveOccurrences(metaModel::referenceModelPropMultiplicity);

        if(parentCObject.getNodeId().equals(childCObject.getNodeId()) && parentNodeOccurrences.equals(childNodeOccurrences)) {
            //this is the parent node appearing in the flattened child archetype without change in occurrence. That is guaranteed to be valid
            return ConformanceCheckResult.conforms();
        }

        List<CObject> allRedefinedNodes = childCObject.getParent().getChildren().stream().filter(
                child -> child.nodeIdConformsTo(parentCObject)
        ).collect(Collectors.toList()); // All child nodes in the child archetype
        MultiplicityInterval allRedefinedNodeOccurrencesSummed = new MultiplicityInterval(0, 0);
        for (CObject childNode : allRedefinedNodes) {
            if (allRedefinedNodeOccurrencesSummed.isOpen()) {
                break;
            }
            MultiplicityInterval redefinedOccurrences = childNode.effectiveOccurrences(metaModel::referenceModelPropMultiplicity);
            if (!allRedefinedNodeOccurrencesSummed.isLowerUnbounded()) {
                Integer lower = allRedefinedNodeOccurrencesSummed.getLower();
                if (!redefinedOccurrences.isLowerUnbounded()) {
                    allRedefinedNodeOccurrencesSummed.setLower(lower + redefinedOccurrences.getLower());
                } else {
                    allRedefinedNodeOccurrencesSummed.setLowerUnbounded(true);
                    allRedefinedNodeOccurrencesSummed.setLower(null);
                }
            }
            if (!allRedefinedNodeOccurrencesSummed.isUpperUnbounded()) {
                Integer upper = allRedefinedNodeOccurrencesSummed.getUpper();
                if (!redefinedOccurrences.isUpperUnbounded()) {
                    allRedefinedNodeOccurrencesSummed.setUpper(upper + redefinedOccurrences.getUpper());
                } else {
                    allRedefinedNodeOccurrencesSummed.setUpperUnbounded(true);
                    allRedefinedNodeOccurrencesSummed.setUpper(null);
                }
            }
        }
        MultiplicityInterval cardinality = childCObject.getParent().getCardinality() == null ? null : childCObject.getParent().getCardinality().getInterval();
        if(cardinality == null) {
            cardinality = metaModel.referenceModelPropMultiplicity(childCObject.getParent().getParent().getRmTypeName(), childCObject.getParent().getRmAttributeName());
        }
        if(cardinality != null && !cardinality.isUpperUnbounded() &&
                (allRedefinedNodeOccurrencesSummed.isUpperUnbounded() || allRedefinedNodeOccurrencesSummed.getUpper() > cardinality.getUpper())) {
            allRedefinedNodeOccurrencesSummed.setUpperUnbounded(false);
            allRedefinedNodeOccurrencesSummed.setUpper(cardinality.getUpper());
        }

        if (parentNodeOccurrences.contains(allRedefinedNodeOccurrencesSummed)) {
            return ConformanceCheckResult.conforms();
        }
        return ConformanceCheckResult.fails(ErrorType.VSONCO, I18n.t("Occurrences {0}, which is the sum of {1}, does not conform to {2}",
                allRedefinedNodeOccurrencesSummed,
                allRedefinedNodes.stream().map(c -> c.effectiveOccurrences(metaModel::referenceModelPropMultiplicity).toString()).collect(Collectors.joining(", ")),
                parentCObject.getOccurrences()));
    }
}
