package com.nedap.archie.template.betterjson;

import com.google.common.collect.Lists;
import com.nedap.archie.adl14.ADL14NodeIDConverter;
import com.nedap.archie.adl14.log.ConvertedCodeResult;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Lots of errors in the better template where new value sets get created that should have been specialised codes. Fix that here.
 */
public class SpecializedTerminologyCodeFixer {

    Map<String, ConvertedCodeResult> convertedCodes;

    public void fixTerminologyCodes(Archetype archetype, FlatArchetypeProvider repo) {
        fixInner(archetype, getFlatParent(archetype, repo));
        if(archetype instanceof Template) {
            Template template = (Template) archetype;
            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                fixInner(overlay, getFlatParent(overlay, repo));
            }
        }

    }

    private Archetype getFlatParent(Archetype archetype, FlatArchetypeProvider repo) {
        Archetype flatParent =  null;
        if(archetype.getParentArchetypeId() != null) {
            flatParent = repo.getFlatArchetype(archetype.getParentArchetypeId());
        }
        return flatParent;
    }

    private void fixInner(Archetype archetype, Archetype flatParent) {
        if(flatParent == null) {
            return;
        }
        convertedCodes = new LinkedHashMap<>();
        fixInner(archetype, archetype.getDefinition(), flatParent);
        ADL14NodeIDConverter.convertTermDefinitions(archetype, convertedCodes);
    }

    private void fixInner(Archetype archetype, CObject cObject, Archetype flatParent) {
        if(cObject instanceof CTerminologyCode) {
            CTerminologyCode cTerminologyCode = (CTerminologyCode) cObject;
            if(cTerminologyCode.getConstraint() != null && cTerminologyCode.getConstraint().size() > 0) {
                String constraint = cTerminologyCode.getConstraint().get(0);
                String pathInParent = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), flatParent.specializationDepth());
                ArchetypeModelObject parentCObject = flatParent.itemAtPath(pathInParent);
                if(AOMUtils.getSpecializationDepthFromCode(constraint) == archetype.specializationDepth()) {
                    if(parentCObject instanceof CTerminologyCode) {
                        fixCTerminologyCode(archetype, cTerminologyCode, constraint, (CTerminologyCode) parentCObject);
                    } else if (parentCObject instanceof CAttribute) {
                        CAttribute parentAttribute = (CAttribute) parentCObject;
                        Optional<CObject> parent = parentAttribute.getChildren().stream().filter(a -> a instanceof CTerminologyCode).findFirst();
                        if(parent.isPresent()) {
                            fixCTerminologyCode(archetype, cTerminologyCode, constraint, (CTerminologyCode) parent.get());
                        }
                    }
                } else if(AOMUtils.getSpecializationDepthFromCode(constraint) < archetype.specializationDepth() &&
                        AOMUtils.isValueCode(constraint)) {
                    if(parentCObject instanceof CTerminologyCode) {
                        fixTerminologyCode2(archetype, flatParent, cTerminologyCode, constraint, (CTerminologyCode) parentCObject);
                    } else if (parentCObject instanceof CAttribute) {
                        CAttribute parentAttribute = (CAttribute) parentCObject;
                        Optional<CObject> parent = parentAttribute.getChildren().stream().filter(a -> a instanceof CTerminologyCode).findFirst();
                        if (parent.isPresent()) {
                            fixTerminologyCode2(archetype, flatParent, cTerminologyCode, constraint, (CTerminologyCode) parent.get());
                        }
                    }
                }
            }

        }
        for(CAttribute attribute:cObject.getAttributes()) {
            fixInner(archetype, attribute, flatParent);
        }
    }

    private void fixTerminologyCode2(Archetype archetype, Archetype flatParent, CTerminologyCode cTerminologyCode, String constraint, CTerminologyCode parentCObject) {
        CTerminologyCode codeInParent = parentCObject;
        if (codeInParent.getConstraint() != null && codeInParent.getConstraint().size() > 0) {
            String constraintInParent = codeInParent.getConstraint().get(0);
            if (AOMUtils.isValueCode(constraint) && AOMUtils.isValueSetCode(constraintInParent)) {
                //this is invalid. Let's fix it!
                //current archetype a single code, parent archetype a value set
                //let's assume the code is from the value set, otherwise the validator will check it anyway
                String newCode = archetype.generateNextSpecializedIdCode(constraintInParent);
                cTerminologyCode.getConstraint().set(0, newCode);

                ValueSet valueSet = new ValueSet();
                valueSet.setId(newCode);
                valueSet.setMembers(Lists.newArrayList(constraint));
                archetype.getTerminology().getValueSets().put(newCode, valueSet);

                addTermForValueSet(archetype, flatParent.getTerm(codeInParent, constraintInParent,
                        flatParent.getOriginalLanguage() == null ? "en" : flatParent.getOriginalLanguage().getCodeString()),
                        valueSet);

            }
        }
    }

    private void fixCTerminologyCode(Archetype archetype, CTerminologyCode cTerminologyCode, String constraint, CTerminologyCode parentCObject) {
        CTerminologyCode codeInParent = parentCObject;
        if (codeInParent.getConstraint() != null && codeInParent.getConstraint().size() > 0) {
            String constraintInParent = codeInParent.getConstraint().get(0);
            if (AOMUtils.isValueSetCode(constraint) && AOMUtils.isValueSetCode(constraintInParent) && !AOMUtils.codesConformant(constraint, constraintInParent)) {
                //this is invalid. Let's fix it!

                String newCode = archetype.generateNextSpecializedIdCode(constraintInParent);


                ValueSet valueSet = archetype.getTerminology().getValueSets().remove(constraint);
                if (valueSet != null) {
                    cTerminologyCode.getConstraint().set(0, newCode);
                    //else means validation error
                    archetype.getTerminology().getValueSets().put(newCode, valueSet);
                    valueSet.setId(newCode);
                    convertedCodes.put(constraint, new ConvertedCodeResult(constraint, newCode));
                }



            } else if (AOMUtils.isValueSetCode(constraintInParent) && AOMUtils.isValueCode(constraint)) {
                System.out.println("fix more");
            } else if (AOMUtils.isValueCode(constraintInParent) && AOMUtils.isValueSetCode(constraint)) {
                System.out.println("fix more");
            }

        }
    }


    private void addTermForValueSet(Archetype archetype, ArchetypeTerm termInParent, ValueSet valueSet) {
        for(String language: archetype.getTerminology().getTermDefinitions().keySet()) {
            //TODO: add new archetype term to conversion log!

            ArchetypeTerm newTerm = new ArchetypeTerm();
            newTerm.setCode(valueSet.getId());
            if(termInParent == null) {
                newTerm.setText("unknown valueset name");
                newTerm.setDescription("unknown valueset name");
            } else {
                newTerm.setText(termInParent.getText() + " (synthesised)");
                newTerm.setDescription(termInParent.getDescription() + " (synthesised)");
            }
            archetype.getTerminology().getTermDefinitions().get(language).put(newTerm.getCode(), newTerm);
        }
    }

    private void fixInner(Archetype archetype, CAttribute cAttribute, Archetype flatParent) {

        for(CObject child:cAttribute.getChildren()) {
            fixInner(archetype, child, flatParent);
        }
    }

    private ValueSet findValueSet(Archetype archetype, Set<String> localCodes) {
        for(ValueSet valueSet:archetype.getTerminology().getValueSets().values()) {
            if(valueSet.getMembers().equals(localCodes)) {
                return valueSet;
            }
        }
        return null;
    }

}
