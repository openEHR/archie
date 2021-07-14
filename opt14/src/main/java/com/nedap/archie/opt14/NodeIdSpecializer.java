package com.nedap.archie.opt14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.query.AOMPathQuery;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;

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

    public void specializeNodeIds(Archetype archetype, FlatArchetypeProvider repo) {
        if(archetype.getParentArchetypeId() == null) {
            return;
        }
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


    private void specializeNodeIds(CObject cObject) {
        if(!(cObject instanceof CPrimitiveObject)) {
            String flatPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), flatParent.specializationDepth());
            CObject parentCObject = getCObject(flatParent.itemAtPath(flatPath));

            if(parentCObject != null) {
                ArchetypeTerm term = archetype.getTerm(cObject, archetype.getOriginalLanguage().getCodeString());
                ArchetypeTerm parentTerm = flatParent.getTerm(parentCObject, archetype.getOriginalLanguage().getCodeString());
                if( cObject.getNodeId().equalsIgnoreCase(parentCObject.getNodeId())) {
                    if(parentTerm != null && term != null) {
                        if (!term.getText().equalsIgnoreCase(parentTerm.getText()) ||
                                !term.getDescription().equalsIgnoreCase(parentTerm.getDescription())) {
                            //term changes. We need a new node id
                            //TODO
                        }
                    }
                }

            } else {
                //someone added a new node. It wil have a specialized id already - or it should anyway. let's check!
                if(AOMUtils.getSpecializationDepthFromCode(cObject.getNodeId()) != flatParent.specializationDepth() +1) {
                    System.out.println("change of code!");//TODO
                }
               // throw new RuntimeException("I did not expect the Spanish inquisition!");//TODO: remove or add proper message
            }

            for(CAttribute attribute:cObject.getAttributes()) {
                specializeNodeIds(attribute);
            }
            changeNameConstraintToArchetypeTerm(cObject);
        }

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

        for(CObject child:attribute.getChildren()) {
            specializeNodeIds(child);
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
