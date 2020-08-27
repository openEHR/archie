package com.nedap.archie.archetypevalidator.validations;

import com.google.common.base.Joiner;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.NodeIdUtil;
import com.nedap.archie.aom.utils.CodeRedefinitionStatus;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import com.nedap.archie.rules.Assertion;
import org.openehr.utils.message.I18n;

import java.util.List;

public class SpecializedDefinitionValidation extends ValidatingVisitor {
    public SpecializedDefinitionValidation() {
        super();
    }

    @Override
    public void validate() {
        //only run these if the archetype is specialized and the parent has been found and flattened
        if(archetype.isSpecialized() && flatParent != null) {
            super.validate();
        } else if (archetype.isSpecialized() && flatParent == null) {
            addMessage(ErrorType.VASID,
                    I18n.t("Parent archetype {0} not found or can not be flattened", archetype.getParentArchetypeId()));
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
            addMessageWithPath(ErrorType.OTHER, cObject.path(),
                    I18n.t("Could not find parent object for {0} but it should have been prechecked. Could you report this as a bug?", flatPath));
            //stop validating if we don't have a parent
            return;
        }


        if(parentCObject instanceof ArchetypeSlot) {
            if(((ArchetypeSlot) parentCObject).isClosed()) {
                //not in the eiffel code, but is in the spec and makes sense: cannot redefined a closed parent slot
                addMessageWithPath(ErrorType.VDSSP, cObject.path(),
                        I18n.t("Cannot redefine a closed archetype slot")
                        );
                passed = false;
            }
        }
        if(cObject instanceof ArchetypeSlot) {
            ArchetypeSlot slot = (ArchetypeSlot) cObject;
            if(slot.isClosed() && (hasAssertions(slot.getExcludes()) || hasAssertions(slot.getIncludes()))) {
                //not in eiffel code, but slot cannot be both closed and narrowed at the same time.
                //we could make this phase 1, but makes sense here
                addMessageWithPath(ErrorType.VDSSC, cObject.path(),
                        I18n.t("A closed archetype slot cannot have its includes or excludes assertions modified"));
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
                //TODO: what does this mean?
                addMessageWithPath(ErrorType.VSUNT, cObject.path());//TODO: check that our flattener actually supports this
                passed = false;
            }
        } else if (parentCObject instanceof  CComplexObject && ((CComplexObject) parentCObject).isAnyAllowed()) {
            //any allowed in parent, so fine here!
            passed = true;
        } else if (!cObject.getClass().equals(parentCObject.getClass())) {
            addMessageWithPath(ErrorType.VSONT, cObject.path(),
                    I18n.t("A {0} cannot specialize a {1}", cObject.getClass().getSimpleName(),
                            parentCObject.getClass().getSimpleName()));
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
                addMessageWithPath(ErrorType.VSONCT, cObject.path(),
                        I18n.t("Type {0} does not conform to type {1} in parent", cObject.getRmTypeName(), parentCObject.getRmTypeName()));
            } else if (!cObject.occurrencesConformsTo(parentCObject)) {
                addMessageWithPath(ErrorType.VSONCO, cObject.path(),
                        I18n.t("Occurrences {0} does not conform to occurrences {1} in parent", cObject.getOccurrences(), parentCObject.getOccurrences()));
            } else if (!cObject.nodeIdConformsTo(parentCObject)) {
                addMessageWithPath(ErrorType.VSONI, cObject.path(),
                        I18n.t("Node ID {0} does not conform to node id {1} in parent", cObject.getNodeId(), parentCObject.getNodeId()));
            } else if (cObject instanceof CPrimitiveObject && parentCObject instanceof CPrimitiveObject) {
                addMessageWithPath(ErrorType.VPOV, cObject.path(),
                        I18n.t("Primitive object with RM type {0} does not conform to primitive object with RM type {1} in parent",
                                cObject.getRmTypeName(),
                                parentCObject.getRmTypeName()));
            } else {
                addMessageWithPath(ErrorType.VUNK, cObject.path(),
                        I18n.t("Unknown error in conformance of specialized C_OBJECT"));
            }
        } else {
            if (cObject instanceof CComplexObject && parentCObject instanceof  CComplexObject) {
                CComplexObject cComplexObject = (CComplexObject) cObject;
                CComplexObject parentCComplexObject = (CComplexObject) parentCObject;
                if(cComplexObject.getAttributeTuples() != null && parentCComplexObject.getAttributeTuples() != null) {
                    for(CAttributeTuple tuple:cComplexObject.getAttributeTuples()) {
                        CAttributeTuple matchingTuple = AOMUtils.findMatchingTuple(parentCComplexObject.getAttributeTuples(), tuple);
                        if(matchingTuple != null && ! tuple.cConformsTo(matchingTuple, combinedModels::rmTypesConformant)) {

                            //tuple does not conform
                            addMessageWithPath(ErrorType.VTPNC, cObject.path(),
                                    I18n.t("Attribute tuple with members {0} does not conform to parent attribute tuple", Joiner.on(", ").join(tuple.getMemberNames())));
                        } else {
                            for(CAttribute attribute:tuple.getMembers()) {
                                CAttribute parentAttribute = parentCComplexObject.getAttribute(attribute.getRmAttributeName());
                                if(parentAttribute != null  && parentAttribute.getSocParent() == null) {
                                    for(CPrimitiveTuple primitiveTuple:tuple.getTuples()) {
                                        CPrimitiveObject member = primitiveTuple.getMember(tuple.getMemberIndex(attribute.getRmAttributeName()));
                                        if(!hasConformingParent(parentAttribute, member)) {
                                            addMessageWithPath(ErrorType.VTPIN, attribute.getPath(),
                                                    I18n.t("Attribute {0} is a non-tuple attribute in the parent archetype, but a tuple attribute in the current archetype. That is not allowed", attribute.getRmAttributeName()));
                                        }

                                    }

                                }
                            }
                        }
                    }
                }
            }
        }


    }

    private boolean hasConformingParent(CAttribute parentAttribute, CPrimitiveObject<?, ?> member) {
        for(CObject parentCObject:parentAttribute.getChildren()) {
            if(member.cConformsTo(parentCObject, (a, b) -> combinedModels.rmTypesConformant(a, b))) {
                return true;
            }
        }
        return false;
    }

    private boolean validateRootSpecializedWithRoot(CArchetypeRoot parentCObject, CArchetypeRoot cObject) {
        if(cObject.getArchetypeRef() == null && cObject.getOccurrences() != null && cObject.getOccurrences().isProhibited()) {
            return true;//prohibiting archetype roots with another archetype root does not require the archetype ref
        } else {
            Archetype usedArchetype = repository.getArchetype(cObject.getArchetypeRef());
            if (usedArchetype != null) {
                if (!repository.isChildOf(repository.getArchetype(parentCObject.getArchetypeRef()), usedArchetype)) {
                    addMessageWithPath(ErrorType.VARXAV, cObject.path(),
                            I18n.t("Use_archetype specializes an archetype root pointing to {0}, but the archetype {1} is not a descendant",
                                    parentCObject.getArchetypeRef(), usedArchetype.getArchetypeId()));
                    return false;
                }
            } else {
                addMessageWithPath(ErrorType.VARXRA, cObject.path(),
                        I18n.t("Use_archetype references archetype id {0}, but no archetype was found", cObject.getArchetypeRef()));
                return false;
            }
            return true;
        }
    }

    private boolean validateSlotSpecializedWithSlot(ArchetypeSlot parentCObject, ArchetypeSlot cObject) {
        if(!parentCObject.getNodeId().equals(cObject.getNodeId())) {
            addMessageWithPath(ErrorType.VDSSID, cObject.path(),
                    I18n.t("A specialized archetype slot must have the same id as the parent id {0}, but it was {1}",
                            parentCObject.getNodeId(), cObject.getNodeId()));
            return false;
        }
        return true;
    }

    private boolean validateSlotSpecializedWithRoot(ArchetypeSlot slot, CArchetypeRoot root) {
        if(!rootMatchesSlotType(slot, root)) {
            addMessageWithPath(ErrorType.VARXS, root.path(),
                    I18n.t("The use_archetype with type {0} does not match the archetype slow (allow_archetype) with type {1}",
                            slot.getRmTypeName(), root.getRmTypeName()));
            return false;
        }

        if(repository.getArchetype(root.getArchetypeRef()) == null) {
            addMessageWithPath(ErrorType.VARXR, root.path(),
                    I18n.t("Use_archetype references archetype id {0}, but no archetype was found", root.getArchetypeRef()));
            return false;
        } else if (AOMUtils.getSpecializationDepthFromCode(root.getNodeId()) != archetype.specializationDepth()) {
            addMessageWithPath(ErrorType.VARXID, root.getPath(),
                    I18n.t("Node ID {0} specialization depth does not conform to the archetype specialization depth {1}", root.getNodeId(), archetype.specializationDepth()));
            return false;
        } else if (!AOMUtils.archetypeIdMatchesSlotExpression(root.getArchetypeRef(), slot)) {
            addMessageWithPath(ErrorType.VARXS, root.path(),
                    I18n.t("Use_archetype {0} does not match the expression of the archetype slot it specialized in the parent", root.getArchetypeRef()));
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
                                addMessageWithPath(ErrorType.VSONPT, cObject.path(),
                                        I18n.t("C_OBJECT in this archetype with class {0} is prohibited, which means its class must be the same as parent type {1}",
                                                cObject.getClass().getSimpleName(), parentCObject.getClass().getSimpleName()));
                            } else if(!parentCObject.getNodeId().equals(cObject.getNodeId())) {
                                addMessageWithPath(ErrorType.VSONPI, cObject.path(),
                                        I18n.t("C_OBJECT in this archetype with node id {0} is prohibited, which means its node id must be the same as parent {1}",
                                                cObject.getNodeId(), parentCObject.getNodeId()));
                            }
                        }
                    } else if(!(cObject instanceof CPrimitiveObject)) {
                        addMessageWithPath(ErrorType.VSONIN, cObject.path(),
                                I18n.t("Node id {0} is not valid here", cObject.getNodeId())
                                );
                    }
                } else if(AOMUtils.getSpecialisationStatusFromCode(cObject.getNodeId(), cObject.specialisationDepth()) == CodeRedefinitionStatus.REDEFINED) {
                    addMessageWithPath(ErrorType.VSONIN, cObject.path(),
                            I18n.t("Node id {0} is not valid here because it redefines an object illegally", cObject.getNodeId()));
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
                            addMessageWithPath(ErrorType.VSSM, cObject.path(),
                                I18n.t("Sibling order {0} refers to missing node id", cObject.getSiblingOrder()));
                        }
                    }
                }

                if(cObject.isProhibited()) {
                    //should be 'attribute_at_path'
                    addMessageWithPath(ErrorType.VSONPO, cObject.path(),
                            I18n.t("An object with the new node id {0} cannot be prohibited", cObject.getNodeId()));
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
