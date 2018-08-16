package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.NodeIdUtil;
import com.nedap.archie.aom.utils.RedefinitionStatus;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ArchetypeValidatingVisitor;
import com.nedap.archie.rules.Assertion;

import java.util.List;

public class SpecializedDefinitionValidation extends ArchetypeValidatingVisitor {
    public SpecializedDefinitionValidation() {
        super();
    }

    @Override
    public void validate() {
        //only run these if the archetype is specialized and the parent has been found and flattened
        if(archetype.isSpecialized() && flatParent != null) {
            super.validate();
        } else if (archetype.isSpecialized() && flatParent == null) {
            addMessage(ErrorType.VASID, "parent archetype not found or can not be flattened");
        }
    }

    @Override
    public void validate(CObject cObject) {
        boolean nodeOk = checkSpecializedNodeHasMatchingPathInParent(cObject);
        if(nodeOk) {
            checkSpecializedNode(cObject);
        }

    }

    private void checkSpecializedNode(CObject cObject) {
        String flatPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), flatParent.specializationDepth());
        CObject parentCObject = getCObject(flatParent.itemAtPath(flatPath));
        boolean passed = true;
        if(parentCObject == null) {
            //shouldn't happen
            addMessageWithPath(ErrorType.OTHER, cObject.path(), String.format("Could not find parent object for %s but it should have been prechecked. Could you report this as a bug?", flatPath));
            //stop validating if we don't have a parent
            return;
        }


        if(parentCObject instanceof ArchetypeSlot) {
            if(((ArchetypeSlot) parentCObject).isClosed()) {
                //not in the eiffel code, but is in the spec and makes sense: cannot redefined a closed parent slot
                addMessageWithPath(ErrorType.VDSSP, cObject.path());
                passed = false;
            }
        }
        if(cObject instanceof ArchetypeSlot) {
            ArchetypeSlot slot = (ArchetypeSlot) cObject;
            if(slot.isClosed() && (hasAssertions(slot.getExcludes()) || hasAssertions(slot.getIncludes()))) {
                //not in eiffel code, but slot cannot be both closed and narrowed at the same time.
                //we could make this phase 1, but makes sense here
                addMessageWithPath(ErrorType.VDSSC, cObject.path());
                passed = false;
            }
        }


        if(cObject instanceof CArchetypeRoot && parentCObject instanceof ArchetypeSlot) {
            passed = validateSlotSpecializedWithRoot((ArchetypeSlot) parentCObject, (CArchetypeRoot) cObject);
        } else if (cObject instanceof ArchetypeSlot && parentCObject instanceof ArchetypeSlot) {
            passed = validateSlotSpecializedWithSlot((ArchetypeSlot) parentCObject, (ArchetypeSlot) cObject);
        } else if(cObject instanceof CArchetypeRoot && parentCObject instanceof  CArchetypeRoot) {
            passed = validateRootSpecializedWithRoot((CArchetypeRoot) parentCObject, (CArchetypeRoot) cObject);
        } else if (cObject instanceof CComplexObject && parentCObject instanceof CComplexObjectProxy) {
            CObject usedNodeInParent = flatParent.itemAtPath(((CComplexObjectProxy) parentCObject).getTargetPath());
            if(usedNodeInParent != null) {
                parentCObject = usedNodeInParent;
            } else {
                addMessageWithPath(ErrorType.VSUNT, cObject.path());//TODO: check that our flattener actually supports this
                passed = false;
            }
        } else if (parentCObject instanceof  CComplexObject && ((CComplexObject) parentCObject).isAnyAllowed()) {
            //any allowed in parent, so fine here!
            passed = true;
        } else if (!cObject.getClass().equals(parentCObject.getClass())) {
            addMessageWithPath(ErrorType.VSONT, cObject.path());
            passed = false;
        }

        if(passed) {
            validateConformsTo(cObject, parentCObject);
        }
    }

    private boolean hasAssertions(List<Assertion> assertions) {
        return assertions != null && !assertions.isEmpty();
    }

    private void validateConformsTo(CObject cObject, CObject parentCObject) {

        if(!cObject.cConformsTo(parentCObject, combinedModels::rmTypesConformant)) {
            if(!cObject.typeNameConformsTo(parentCObject, combinedModels::rmTypesConformant)) {
                addMessageWithPath(ErrorType.VSONCT, cObject.path());
            } else if (!cObject.occurrencesConformsTo(parentCObject)) {
                addMessageWithPath(ErrorType.VSONCO, cObject.path());
            } else if (!cObject.nodeIdConformsTo(parentCObject)) {
                addMessageWithPath(ErrorType.VSONI, cObject.path());
            } else if (cObject instanceof CPrimitiveObject && parentCObject instanceof CPrimitiveObject) {
                addMessageWithPath(ErrorType.VPOV, cObject.path());
            } else {
                addMessageWithPath(ErrorType.VUNK, cObject.path());
            }
        } else {
            if (cObject instanceof CComplexObject && parentCObject instanceof  CComplexObject) {
                CComplexObject cComplexObject = (CComplexObject) cObject;
                CComplexObject parentCComplexObject = (CComplexObject) parentCObject;
                if(cComplexObject.getAttributeTuples() != null && parentCComplexObject.getAttributeTuples() != null) {
                    for(CAttributeTuple tuple:cComplexObject.getAttributeTuples()) {
                        CAttributeTuple matchingTuple = AOMUtils.findMatchingTuple(parentCComplexObject.getAttributeTuples(), tuple);
                        if(matchingTuple != null && ! tuple.cConformsTo(matchingTuple, combinedModels::rmTypesConformant)) {
                            addMessageWithPath(ErrorType.VTPNC, cObject.path());
                        } else {
                            for(CAttribute attribute:tuple.getMembers()) {
                                CAttribute parentAttribute = parentCComplexObject.getAttribute(attribute.getRmAttributeName());
                                if(parentAttribute != null  && parentAttribute.getSocParent() == null) {
                                    addMessageWithPath(ErrorType.VTPIN, attribute.getPath());
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    private boolean validateRootSpecializedWithRoot(CArchetypeRoot parentCObject, CArchetypeRoot cObject) {
        if(cObject.getArchetypeRef() == null && cObject.getOccurrences() != null && cObject.getOccurrences().isProhibited()) {
            return true;//prohibiting archetype roots with another archetype root does not require the archetype ref
        } else {
            Archetype usedArchetype = repository.getArchetype(cObject.getArchetypeRef());
            if (usedArchetype != null) {
                if (!repository.isChildOf(repository.getArchetype(parentCObject.getArchetypeRef()), usedArchetype)) {
                    addMessage(ErrorType.VARXAV, cObject.path());
                    return false;
                }
            } else {
                addMessageWithPath(ErrorType.VARXRA, cObject.path());
                return false;
            }
            return true;
        }
    }

    private boolean validateSlotSpecializedWithSlot(ArchetypeSlot parentCObject, ArchetypeSlot cObject) {
        if(!parentCObject.getNodeId().equals(cObject.getNodeId())) {
            addMessageWithPath(ErrorType.VDSSID, cObject.path());
            return false;
        }
        return true;
    }

    private boolean validateSlotSpecializedWithRoot(ArchetypeSlot slot, CArchetypeRoot root) {
        if(!rootMatchesSlotType(slot, root)) {
            addMessageWithPath(ErrorType.VARXS, root.path());
            return false;
        }
        if(repository.getArchetype(root.getArchetypeRef()) == null) {
            addMessageWithPath(ErrorType.VARXR, root.path());
            return false;
        } else if (AOMUtils.getSpecializationDepthFromCode(root.getNodeId()) != archetype.specializationDepth()) {
            addMessageWithPath(ErrorType.VARXID, root.getPath());
            return false;
        } else if (!AOMUtils.archetypeIdMatchesSlotExpression(root.getArchetypeRef(), slot)) {
            addMessageWithPath(ErrorType.VARXS, root.path());
            return false;
        }
        return true;

    }

    private boolean rootMatchesSlotType(ArchetypeSlot slot, CArchetypeRoot root) {
        String slotRmTypeName = slot.getRmTypeName();
        String rootRmTypeName = root.getRmTypeName();
        String rootReferenceRmTypeName = new ArchetypeHRID(root.getArchetypeRef()).getRmClass();

        if(!combinedModels.typeNameExists(rootRmTypeName) || !combinedModels.typeNameExists(rootReferenceRmTypeName)) {
            return false;
        }
        else if(!combinedModels.rmTypesConformant(rootRmTypeName, slotRmTypeName)) {
            return false;
        } else if (!combinedModels.rmTypesConformant(rootReferenceRmTypeName, slotRmTypeName)) {
            return false;
        }
        return true;
    }

    /**
     * Complicated method - does validations and returns true if the given node has a matching path in the parent
     * @param cObject
     * @return
     */
    private boolean checkSpecializedNodeHasMatchingPathInParent(CObject cObject) {
        boolean result = false;
        if(cObject.isRootNode() || !cObject.getParent().isSecondOrderConstrained()) {
            if(AOMUtils.getSpecializationDepthFromCode(cObject.getNodeId()) <= flatParent.specializationDepth()
                    || new NodeIdUtil(cObject.getNodeId()).isRedefined()) {
                if(!AOMUtils.isPhantomPathAtLevel(cObject.getPathSegments(), flatParent.specializationDepth())) {
                    String flatPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), flatParent.specializationDepth());
                    CObject parentCObject = getCObject(flatParent.itemAtPath(flatPath));
                    result = parentCObject != null;
                    if(parentCObject != null) {
                        if(cObject.isProhibited()) {
                            if(!cObject.getClass().equals(parentCObject.getClass())) {
                                addMessage(ErrorType.VSONPT, cObject.path());
                            } else if(!parentCObject.getNodeId().equals(cObject.getNodeId())) {
                                addMessage(ErrorType.VSONPI, cObject.path());
                            }
                        }
                    } else if(!(cObject instanceof CPrimitiveObject)) {
                        addMessage(ErrorType.VSONIN, cObject.path());
                    }
                } else if(AOMUtils.getSpecialisationStatusFromCode(cObject.getNodeId(), cObject.specialisationDepth()) == RedefinitionStatus.REDEFINED) {
                    //TODO method in if not yet implemented
                    addMessage(ErrorType.VSONIN, cObject.path());
                }
            } else {
                //special checks if it is a non-overlay node...
                //- if it has a sibling order, check that the sibling order refers to a valid node in the flat ancestor.
                if(cObject.getSiblingOrder() != null) {
                    CAttribute parentAttribute = flatParent.itemAtPath(AOMUtils.pathAtSpecializationLevel(cObject.getParent().getPathSegments(), flatParent.specializationDepth()));
                    if(parentAttribute != null) { //null check is handled by VDIFP
                        //TODO: although this is a bit strange, nodeid could be unspecialized, parent can be specialized
                        CObject child = parentAttribute.getChild(cObject.getSiblingOrder().getSiblingNodeId());
                        CObject child2 = parentAttribute.getChild(AOMUtils.codeAtLevel(cObject.getSiblingOrder().getSiblingNodeId(), flatParent.specializationDepth()));
                        if (child == null && child2 == null) {
                            addMessage(ErrorType.VSSM, cObject.path());
                        }
                    }
                }

                if(cObject.isProhibited()) {
                    //should be 'attribute_at_path'

                    addMessage(ErrorType.VSONPO, cObject.path());
                }
            }
        }//else in eiffel code is separate cattribute method here
        return result;
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


    @Override
    public void validate(CAttribute attribute) {
        SpecializedAttributeValidation specializedAttributeValidation = new SpecializedAttributeValidation();
        if(specializedAttributeValidation.validateTest(attribute, this)) {
            specializedAttributeValidation.validate(attribute, this);
        }


    }


}
