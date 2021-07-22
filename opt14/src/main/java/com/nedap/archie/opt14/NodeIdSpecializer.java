package com.nedap.archie.opt14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.diff.PrimitiveObjectEqualsChecker;
import com.nedap.archie.flattener.FlattenerUtil;
import com.nedap.archie.query.AOMPathQuery;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.MetaModels;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Walks the archetype tree, then specializes any node ids it finds with change where required. Can be:
 * - different type
 * - different text or description
 * - different primitive node child (TODO: check how in Differentiator?)
 *
 *
 * TODO: remove simple single constraint name constraints, and put them in the terminology instead first!
 *
 */
public class NodeIdSpecializer {

    private Archetype archetype;
    private Archetype flatParent;
    private MetaModels metaModels;

    public NodeIdSpecializer(MetaModels metaModels) {
        this.metaModels = metaModels;
    }

    public void specializeNodeIds(Archetype archetype, FlatArchetypeProvider repo) {
        if(archetype.getParentArchetypeId() == null) {
            return;
        }
        metaModels.selectModel(archetype);
        this.archetype = archetype;
        if(archetype.getParentArchetypeId() != null) {
            this.flatParent = repo.getFlatArchetype(archetype.getParentArchetypeId());
        }
        specializeNodeIds(archetype.getDefinition());

        if(archetype instanceof Template) {
            Template template = (Template) archetype;

            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                this.archetype = overlay;
                if(archetype.getParentArchetypeId() != null) {
                    this.flatParent = repo.getFlatArchetype(overlay.getParentArchetypeId());
                }

                specializeNodeIds(overlay.getDefinition());
            }
        }
    }


    /**
     * Specialize the node ids of cObject. Returns to prevent ConcurrentModificationExceptions
     * @param cObject the object to check and specialize
     * @return null if no object should be added to the parent right after current object, non-null to indicate it must be added
     */
    private CObject specializeNodeIds(CObject cObject) {
        CObject parentReplacement = null;
        if(!(cObject instanceof CPrimitiveObject)) {
            String flatPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), flatParent.specializationDepth());
            CObject parentCObject = getCObject(flatParent.itemAtPath(flatPath));

            if(parentCObject != null) {
                ArchetypeTerm term = archetype.getTerm(cObject, archetype.getOriginalLanguage().getCodeString());
                ArchetypeTerm parentTerm = flatParent.getTerm(parentCObject, archetype.getOriginalLanguage().getCodeString());
                if ( cObject.getNodeId().equalsIgnoreCase(parentCObject.getNodeId())) {
                    boolean createSpecializedObject = false;
                    if ( parentTerm != null ) {
                        if (term != null) {
                            if (!term.getText().equalsIgnoreCase(parentTerm.getText()) ||
                                    !term.getDescription().equalsIgnoreCase(parentTerm.getDescription())) {
                                createSpecializedObject = true;
                            }
                        }
                    }
                    if (cObjectHasChangedPrimitiveChildren(cObject, parentCObject)) {
                        createSpecializedObject = true;
                    }
                    if(cObjectIsSpecializedArchetypeRoot(cObject, parentCObject)) {
                        createSpecializedObject = true;
                    }
                    if(cObjectHasTypeNameChange(cObject, parentCObject)) {
                        createSpecializedObject = true;
                    }
                    if(createSpecializedObject) {
                        String newNodeId = archetype.generateNextSpecializedIdCode(cObject.getNodeId());
                        String oldNodeId = cObject.getNodeId();
                        cObject.setNodeId(newNodeId);
                        ArchetypeTerminology terminology = cObject.getArchetype().getTerminology();
                        for (String language : terminology.getTermDefinitions().keySet()) {
                            ArchetypeTerm removed = terminology.getTermDefinitions().get(language).remove(oldNodeId);
                            terminology.getTermDefinitions().get(language).put(newNodeId, removed);
                        }
                        if(!FlattenerUtil.shouldReplaceSpecializedParent(parentCObject, Lists.newArrayList(cObject), metaModels)) {
                            //this node should not replace its parent, but should just be added. however, the OPT indicates it should
                            //so create a new CObject with node id of the parent, occurrences matches {0}
                            if(parentCObject instanceof ArchetypeSlot) {
                                parentReplacement = new ArchetypeSlot();
                                ((ArchetypeSlot) parentReplacement).setClosed(true);
                            } else {
                                parentReplacement = new CComplexObject();
                                parentReplacement.setOccurrences(MultiplicityInterval.createProhibited());
                            }
                            parentReplacement.setNodeId(oldNodeId);
                            parentReplacement.setRmTypeName(parentCObject.getRmTypeName());
                        }
                    }
                }

            } else {
                //someone added a new node. It wil have a specialized id already - or it should anyway. let's check!
                if(AOMUtils.getSpecializationDepthFromCode(cObject.getNodeId()) != flatParent.specializationDepth() +1) {
                    throw new RuntimeException("Template added a field with an incorrect specialization depth at " + cObject.getPath());//TODO: remove or add proper message
                }
               //
            }

            for(CAttribute attribute:cObject.getAttributes()) {
                specializeNodeIds(attribute);
            }
            changeNameConstraintToArchetypeTerm(cObject);
        }
        return parentReplacement;

    }

    private boolean cObjectIsSpecializedArchetypeRoot(CObject cObject, CObject parentCObject) {
        return cObject instanceof CArchetypeRoot && parentCObject instanceof ArchetypeSlot;
    }

    private boolean cObjectHasTypeNameChange(CObject cObject, CObject parentCObject) {
        return !cObject.getRmTypeName().equals(parentCObject.getRmTypeName());
    }

    private boolean cObjectHasChangedPrimitiveChildren(CObject cObject, CObject parentCObject) {
        for(CAttribute attribute:cObject.getAttributes()) {
            int i = 0;
            for(CObject childObject:attribute.getChildren()) {
                if(childObject instanceof CPrimitiveObject) {
                    //found a primitive object. Check if it's exactly the same as the parent
                    CAttribute attributeFromParent = parentCObject.getAttribute(attribute.getRmAttributeName());
                    if(attributeFromParent == null) {
                        //new attribute with primitive content, needs specialization
                        return true;
                    } else {
                        if(i < attributeFromParent.getChildren().size()) {
                            CObject childObjectFromParent = attributeFromParent.getChildren().get(i);
                            if(!(childObjectFromParent instanceof CPrimitiveObject)) {
                                throw new RuntimeException("primitive object being converted is a non primitive object in parent at " + archetype.getArchetypeId() + " " + cObject.getPath());
                            }
                            if(!primitiveObjectEquals( (CPrimitiveObject) childObject, (CPrimitiveObject) childObjectFromParent)) {
                                return true;
                            }
                        } else {
                            //more cobjects than the parent has.
                            return true;
                        }
                    }
                }
                i++;
            }
        }
        return false;
    }

    private boolean primitiveObjectEquals(CPrimitiveObject childObject, CPrimitiveObject childObjectFromParent) {
        PrimitiveObjectEqualsChecker.isEqual(childObject, childObjectFromParent);
        return true;
    }

    /**
     * replace all 'name matches DV_TEXT { value matches {"some static name"}}' constraints to archtype terms
     * @param cObject
     */
    private void changeNameConstraintToArchetypeTerm(CObject cObject) {
        List<Integer> attributesToRemove = new ArrayList<>();
        int i = 0;
        for(CAttribute attribute:cObject.getAttributes()) {
            if(attribute.getRmAttributeName().equalsIgnoreCase("name")) {
                //ok a name constraint. If it's a simple constraint, replace it with a terminology entry please.
                if(attribute.getChildren().size() == 1 && attribute.getChildren().get(0).getRmTypeName().equalsIgnoreCase("DV_TEXT")) {
                    CObject nameCObject = attribute.getChildren().get(0);
                    Object o = new AOMPathQuery("/value[1]").find((CComplexObject) nameCObject);
                    if(o instanceof CString) {
                        CString nameConstraint = (CString) o;
                        if (nameConstraint.getConstraint().size() == 1 && !CString.isRegexConstraint(nameConstraint.getConstraint().get(0))) {
                            //remove the crap out of this attribute.
                            attributesToRemove.add(i);
                            ArchetypeTerm term = archetype.getTerm(attribute.getParent(), archetype.getOriginalLanguage().getCodeString());
                            if (term != null) {
                                term.setText((nameConstraint.getConstraint().get(0)));
                            } else {
                                term = new ArchetypeTerm();
                                term.setCode(attribute.getParent().getNodeId());
                                term.setText(nameConstraint.getConstraint().get(0));
                                term.setDescription(nameConstraint.getConstraint().get(0));
                                getOrCreateTermDefinitions().put(attribute.getParent().getNodeId(), term);
                            }
                        }
                    }
                }
            }
            i++;
        }
        Collections.reverse(attributesToRemove);
        for(int index:attributesToRemove) {
            cObject.getAttributes().remove(index);
        }
    }

    private void specializeNodeIds(CAttribute attribute) {

        List<CObject> objectsToAdd = new ArrayList<>();
        for(CObject child:attribute.getChildren()) {
            CObject toAdd = specializeNodeIds(child);
            if(toAdd != null) {
                //add temporary sibling order to be able to add this node in the correct position
                toAdd.setSiblingOrder(SiblingOrder.createAfter(child.getNodeId()));
                objectsToAdd.add(toAdd);
            }
        }
        for(CObject cObject:objectsToAdd) {
            attribute.addChild(cObject, cObject.getSiblingOrder());
            //remove temporary sibling order added above
            cObject.setSiblingOrder(null);
        }
    }

    private Map<String, ArchetypeTerm> getOrCreateTermDefinitions() {
        Map<String, ArchetypeTerm> termDefs =  archetype.getTerminology().getTermDefinitions().get(archetype.getOriginalLanguage().getCodeString());
        if(termDefs == null) {
            termDefs = new LinkedHashMap<>();
            archetype.getTerminology().getTermDefinitions().put(archetype.getOriginalLanguage().getCodeString(), termDefs);
        }
        return termDefs;
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
}
