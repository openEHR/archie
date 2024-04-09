package com.nedap.archie.flattener;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.apath.PathUtil;
import com.nedap.archie.query.AOMPathQuery;
import com.nedap.archie.apath.APathQuery;
import com.nedap.archie.query.ComplexObjectProxyReplacement;

import java.util.*;

/**
 * Flattens attributes, taking sibling order into account.
 */
public class CAttributeFlattener {

    private final IAttributeFlattenerSupport flattener;

    public CAttributeFlattener(IAttributeFlattenerSupport flattener) {
        this.flattener = flattener;
    }

    public void flattenSingleAttribute(CComplexObject newObject, CAttribute attribute) {
        if(attribute.getDifferentialPath() != null) {
            //this overrides a specific path
            ArchetypeModelObject object = new AOMPathQuery(attribute.getDifferentialPath()).dontFindThroughCComplexObjectProxies().find(newObject);
            if(object == null) {
                //it is possible that the object points to a reference, in which case we need to clone the referenced node, then try again
                //AOM spec paragraph 7.2: 'proxy reference targets are expanded inline if the child archetype overrides them.'
                //also examples in ADL2 spec about internal references
                //so find the internal references here!
                //TODO: AOMUtils.pathAtSpecializationLevel(pathSegments.subList(0, pathSegments.size()-1), flatParent.specializationDepth());
                CComplexObjectProxy internalReference = new AOMPathQuery(attribute.getDifferentialPath()).findAnyInternalReference(newObject);
                if(internalReference != null) {
                    //in theory this can be a use node within a use node.
                    ComplexObjectProxyReplacement complexObjectProxyReplacement =
                            ComplexObjectProxyReplacement.getComplexObjectProxyReplacement(internalReference);
                    if(complexObjectProxyReplacement != null) {
                        complexObjectProxyReplacement.replace();
                        //and again!
                        flattenSingleAttribute(newObject, attribute);
                    } else {
                        throw new RuntimeException("cannot find target in CComplexObjectProxy");
                    }
                } else {
                    //lookup the parent and try to add the last attribute if it does not exist
                    List<PathSegment> pathSegments = new APathQuery(attribute.getDifferentialPath()).getPathSegments();
                    String pathMinusLastNode = PathUtil.getPath(pathSegments.subList(0, pathSegments.size()-1));
                    CObject parentObject = newObject.itemAtPath(pathMinusLastNode);
                    if(parentObject != null && parentObject instanceof CComplexObject) {
                        //attribute does not exist, but does exist in RM (or it would not have passed the ArchetypeValidator, or the person using
                        //this flattener does not care
                        CAttribute realAttribute = new CAttribute(pathSegments.get(pathSegments.size()-1).getNodeName());
                        ((CComplexObject) parentObject).addAttribute(realAttribute);
                        flattenAttribute(newObject, realAttribute, attribute);
                    }

                }
            }
            else if(object instanceof CAttribute) {
                CAttribute realAttribute = (CAttribute) object;
                flattenAttribute(newObject, realAttribute, attribute);
            } else if (object instanceof CObject) {
                //TODO: what does this mean?
            }

        } else {
            //this overrides the same path
            flattenAttribute(newObject, newObject.getAttribute(attribute.getRmAttributeName()), attribute);
        }
    }

    public CAttribute flattenAttribute(CComplexObject root, CAttribute attributeInParent, CAttribute attributeInSpecialization) {
        if(attributeInParent == null) {
            CAttribute childCloned = attributeInSpecialization.clone();
            root.addAttribute(childCloned);
            return childCloned;
        } else {

            int specializationLevelOfParent = attributeInSpecialization.getArchetype().specializationDepth()-1;
            attributeInParent.setExistence(FlattenerUtil.getPossiblyOverridenValue(attributeInParent.getExistence(), attributeInSpecialization.getExistence()));
            attributeInParent.setCardinality(FlattenerUtil.getPossiblyOverridenValue(attributeInParent.getCardinality(), attributeInSpecialization.getCardinality()));

            // a list of all node ids that have been excluded in this operation
            Set<String> excludedNodeIds = new HashSet<>();

            if (attributeInSpecialization.getChildren().size() > 0 && attributeInSpecialization.getChildren().get(0) instanceof CPrimitiveObject) {
                //in case of a primitive object, just replace all nodes
                attributeInParent.setChildren(attributeInSpecialization.getChildren());
            } else {

                //ordering the children correctly is tricky.
                // First reorder parentCObjects if necessary, in the case that a sibling order refers to a redefined node
                // in the attributeInSpecialization archetype
                reorderSiblingOrdersReferringToSameLevel(attributeInSpecialization);

                //Now maintain an insertion anchor
                //for when a sibling node has been set somewhere.
                // insert everything after/before the anchor if it is set,
                // at the defined position from the spec if it is null
                SiblingOrder anchor = null;

                List<CObject> parentCObjects = attributeInParent.getChildren();

                for (CObject specializedChildCObject : attributeInSpecialization.getChildren()) {

                    //find matching attributeInParent and create the child node with it
                    CObject matchingParentObject = findMatchingParentCObject(specializedChildCObject, parentCObjects);

                    if(specializedChildCObject.getSiblingOrder() != null) {
                        //new sibling order, update the anchor
                        anchor = specializedChildCObject.getSiblingOrder();
                    }

                    CObject specializedObject = null;
                    if (anchor != null) {
                        specializedObject = mergeObjectIntoAttribute(attributeInParent, specializedChildCObject, matchingParentObject, attributeInSpecialization.getChildren(), anchor);
                        anchor = nextAnchor(anchor, specializedChildCObject);
                    } else { //no sibling order, apply default rules
                        //add to end
                        specializedObject = flattener.createSpecializeCObject(attributeInParent, matchingParentObject, specializedChildCObject);
                        if(matchingParentObject == null) {
                            //extension nodes should be added to the last position
                            attributeInParent.addChild(specializedObject);
                        } else {
                            attributeInParent.addChild(specializedObject, SiblingOrder.createAfter(findLastSpecializedChildDirectlyAfter(attributeInParent, matchingParentObject)));
                            if(shouldRemoveParent(specializedChildCObject, matchingParentObject, attributeInSpecialization.getChildren())) {
                                attributeInParent.removeChild(matchingParentObject.getNodeId());
                            }
                        }
                    }

                    // Checks for
                    if(flattener.getConfig().isAllowSpecializationAfterExclusion() && matchingParentObject != null) {
                        boolean thisNodeIsExclusion = false;
                        if (Objects.equals(specializedObject.getNodeId(), matchingParentObject.getNodeId()) &&
                                specializedObject.getOccurrences() != null && specializedObject.getOccurrences().isProhibited() &&
                                (matchingParentObject.getOccurrences() == null || !matchingParentObject.getOccurrences().isProhibited())
                        ) {
                            excludedNodeIds.add(specializedObject.getNodeId());
                            thisNodeIsExclusion = true;
                        }

                        if (!thisNodeIsExclusion && excludedNodeIds.contains(AOMUtils.codeAtLevel(specializedChildCObject.getNodeId(), specializationLevelOfParent))) {
                            //because of this particular specialization, the parent object was excluded, so occurrences matches {0} in the specialized archetype
                            //but a modification of the parent node is done after that in the specialized archetype
                            specializedObject.setOccurrences(specializedChildCObject.getOccurrences());
                        }
                    }
                }

            }
            return attributeInParent;
        }
    }

    /**
     * Given the last used siblingorder anchor and the last specialized object added, return the SiblingOrder where the
     * next specialized object should be added - if that next object does not have a new sibling order
     * @param lastAnchor
     * @param lastSpecializedObject
     * @return
     */
    private SiblingOrder nextAnchor(SiblingOrder lastAnchor, CObject lastSpecializedObject) {
        if(lastAnchor.isBefore()) {
            return lastAnchor;
        } else {
            return SiblingOrder.createAfter(lastSpecializedObject.getNodeId());
        }
    }

    /**
     * Add the specializedChildObject to the parentAttribute at the given siblingOrder. This method automatically checks if the matchingParentObject should be removed, and removes it if necessary.
     *
     * @param parentAttribute the attribute to add the new object to
     * @param specializedChildCObject the specialized object that should be merged into the parent object
     * @param matchingParentObject the matching parent CObject for the given specializedChildObject
     * @param allSpecializedChildren all the specialized children in the same container as specialedChildCObject
     * @param siblingOrder the sibling order where to add the specializedChild to. Directly adds, no preprocessing or anchor support in this method, you must do that before.
     * @return the created Specialised object, that also has been added to the parentAttribute
     */
    private CObject mergeObjectIntoAttribute(CAttribute parentAttribute, CObject specializedChildCObject, CObject matchingParentObject, List<CObject> allSpecializedChildren, SiblingOrder siblingOrder) {
        CObject specializedObject = flattener.createSpecializeCObject(parentAttribute, matchingParentObject, specializedChildCObject);
        if (shouldRemoveParent(specializedChildCObject, matchingParentObject, allSpecializedChildren)) {
            parentAttribute.removeChild(matchingParentObject.getNodeId());
        }
        parentAttribute.addChild(specializedObject, siblingOrder);
        return specializedObject;
    }


    /**
     * If the following occurs:
     *
     * after[id3]
     * ELEMENT[id2]
     * ELEMENT[id0.1]
     * before[id4]
     * ELEMENT[id3.1]
     *
     * Where id3.1 itself is inside a block reordered with sibling nodes. So, reorder it and remove sibling orders:
     * If a sibling order does not refer to specialized nodes at this level, this function leaves them alone (in this example before[id4])
     *
     * ELEMENT[id3.1]
     * ELEMENT[id2]
     * ELEMENT[id0.1]
     * ELEMENT[id4]
     *
     * @param parent the attribute to reorder the child nodes for
     */
    private void reorderSiblingOrdersReferringToSameLevel(CAttribute parent) {
        SiblingOrder anchor = null;
        for(CObject cObject:new ArrayList<>(parent.getChildren())) {
            if(cObject.getSiblingOrder() != null) {
                //a new sibling order replaces the anchor
                anchor = null;

                String matchingNodeId = findCObjectMatchingSiblingOrder(cObject.getSiblingOrder(), parent.getChildren());
                if(matchingNodeId != null) {
                    parent.removeChild(cObject.getNodeId());
                    SiblingOrder siblingOrder = new SiblingOrder();
                    siblingOrder.setSiblingNodeId(matchingNodeId);
                    siblingOrder.setBefore(cObject.getSiblingOrder().isBefore());
                    anchor = nextAnchor(siblingOrder, cObject);;
                    parent.addChild(cObject, siblingOrder);
                    cObject.setSiblingOrder(null);//unset sibling order, it has been processed already
                }
            } else if (anchor != null) {
                parent.removeChild(cObject.getNodeId());
                parent.addChild(cObject, anchor);
                anchor = nextAnchor(anchor, cObject);
            }
        }
    }

    /**
     * Find the CObject in the given list of cObjects of which the node id is the same or a specialization of the node id in the given sibling order
     * Only returns CObjects that appear after a sibling order, either the sibling order of the CObject itself or one before the resulting CObject
     *
     * Returns null if no matching CObject can be found
     *
     * @param siblingOrder the sibling order to find a match for
     * @param cObjectList the list of CObjects to search
     * @return the node id of the found matching CObject
     */
    private String findCObjectMatchingSiblingOrder(SiblingOrder siblingOrder, List<CObject> cObjectList) {
        SiblingOrder foundSiblingOrder = null;
        for(CObject object:cObjectList) {
            if(foundSiblingOrder != null && AOMUtils.isOverriddenIdCode(object.getNodeId(), siblingOrder.getSiblingNodeId())) {
                return object.getNodeId();
            }
            if(object.getSiblingOrder() != null) {
                foundSiblingOrder = object.getSiblingOrder();
            }
        }
        return null;
    }

    /**
     * Give an attribute and a CObject that is a child of that attribute, find the node id of the last object that is
     * in the list of nodes directly after the child attribute that specialize the matching parent. If the parent
     * has not yet been specialized, returns the parent node id
     *
     * @param parent
     * @param matchingParentObject
     * @return
     */
    private String findLastSpecializedChildDirectlyAfter(CAttribute parent, CObject matchingParentObject) {
        int matchingIndex = parent.getIndexOfChildWithNodeId(matchingParentObject.getNodeId());
        String result = matchingParentObject.getNodeId();
        for(int i = matchingIndex+1; i < parent.getChildren().size(); i++) {
            if(AOMUtils.isOverriddenIdCode(parent.getChildren().get(i).getNodeId(), matchingParentObject.getNodeId())) {
                result = parent.getChildren().get(i).getNodeId();
            }
        }
        return result;
    }

    /**
     * For the given specialized CObject that is to be added to the archetype, specializing matchingParentObject, and given
     * the list of all specialized children of the same container as the given specialized cobject, return if the parent
     * should be removed from the resulting list of flattened CObjects after inserting the specialized check or not.
     *
     *
     * @param specializedChildCObject the specialized child object that is to be added
     * @param matchingParentObject the CObject that matches with the specializedChildObject. Can be null.
     * @param allSpecializedChildren all specialized children under the specializing child container
     * @return
     */
    private boolean shouldRemoveParent(CObject specializedChildCObject, CObject matchingParentObject, List<CObject> allSpecializedChildren) {
        if(matchingParentObject == null) {
            return false;
        }
        List<CObject> allMatchingChildren = new ArrayList<>();
        for (CObject specializedChild : allSpecializedChildren) {
            if (AOMUtils.isOverridenCObject(specializedChild, matchingParentObject)) {
                allMatchingChildren.add(specializedChild);
            }

        }
        boolean hasSameNodeIdInMatchingChildren = allMatchingChildren.stream().anyMatch(c -> c.getNodeId().equals(matchingParentObject.getNodeId()));
        if(hasSameNodeIdInMatchingChildren) {
            //if parent contains id2, and child as well, replace the exact same node with the exact child. Otherwise,
            //add children and replace the last child.
            return specializedChildCObject.getNodeId().equalsIgnoreCase(matchingParentObject.getNodeId());
        } else if(allMatchingChildren.get(allMatchingChildren.size()-1).getNodeId().equalsIgnoreCase(specializedChildCObject.getNodeId())) {
            //the last matching child should possibly replace the parent, the rest should just add
            //if there is just one child, that's fine, it should still work
            return shouldReplaceSpecializedParent(matchingParentObject, allMatchingChildren);
        }
        return false;
    }

    private boolean shouldReplaceSpecializedParent(CObject parent, List<CObject> differentialNodes) {

        MultiplicityInterval occurrences = parent.effectiveOccurrences(flattener.getMetaModels()::referenceModelPropMultiplicity);
        //isSingle/isMultiple is tricky and not doable just in the parser. Don't use those
        if(isSingle(parent.getParent())) {
            return true;
        } else if(occurrences != null && occurrences.upperIsOne()) {
            //REFINE the parent node case 1, the parent has occurrences upper == 1
            return true;
        } else if (differentialNodes.size() == 1) {
            MultiplicityInterval effectiveOccurrences;
            //the differentialNode can have a differential path instead of an attribute name. In that case, we need to replace the rm type name
            //of the parent with the actual typename in the parent archetype. Otherwise, it may fall back to the default type in the RM,
            //and that can be an abstract type that does not have the attribute that we are trying to constrain. For example:
            //diff archetype:
            // /events[id6]/data/items matches {
            //in the rm, data maps to an ITEM_STRUCTURE that does not have the attribute items.
            //in the parent archetype, that is then an ITEM_TREE. We need to use ITEM_TREE here, which is what this code accomplishes.
            if(parent.getParent() == null || parent.getParent().getParent() == null) {
                effectiveOccurrences = differentialNodes.get(0).effectiveOccurrences(flattener.getMetaModels()::referenceModelPropMultiplicity);
            } else {
                effectiveOccurrences = differentialNodes.get(0).effectiveOccurrences((s, s2) -> flattener.getMetaModels().referenceModelPropMultiplicity(
                        parent.getParent().getParent().getRmTypeName(), parent.getParent().getRmAttributeName()));
            }
            if(effectiveOccurrences != null && effectiveOccurrences.upperIsOne()) {
                //REFINE the parent node case 2, only one child with occurrences upper == 1
                return true;
            }
        }
        return false;
    }

    private boolean isSingle(CAttribute attribute) {
        if(attribute != null && attribute.getParent() != null && attribute.getDifferentialPath() == null) {
            return !flattener.getMetaModels().isMultiple(attribute.getParent().getRmTypeName(), attribute.getRmAttributeName());
        }
        return false;
    }

    /**
     * Find the matching parent CObject given a specialized child. REturns null if not found.
     * @param specializedChildCObject
     * @param parentCObjects
     * @return
     */
    private CObject findMatchingParentCObject(CObject specializedChildCObject, List<CObject> parentCObjects) {
        for (CObject parentCObject : parentCObjects) {
            if (AOMUtils.isOverridenCObject(specializedChildCObject, parentCObject)) {
                return parentCObject;
            }
        }
        return null;
    }


}
