package com.nedap.archie.template.betterjson;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.NodeIdUtil;
import com.nedap.archie.base.Interval;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ArchetypeTermFixer {

    private String originalLanguage;

    public void fixTerms(Archetype archetype, FlatArchetypeProvider repo) {
        originalLanguage = archetype.getOriginalLanguage().getCodeString();
        addTerminologyIfNotPresent(archetype);
        fixTerms(archetype,repo, archetype.getDefinition());
        fixValueSetCodes(archetype, repo);

        removeUntranslatedLanguages(archetype);

        if(archetype instanceof Template) {
            Template template = (Template) archetype;
            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                addTerminologyIfNotPresent(overlay);
                fixTerms(overlay, repo, overlay.getDefinition());
                fixValueSetCodes(overlay, repo);
            }
        }
    }

    private void fixValueSetCodes(Archetype archetype, FlatArchetypeProvider repo) {
        String language = originalLanguage;
        if(!archetype.getTerminology().getTermDefinitions().containsKey(originalLanguage)) {
            language = archetype.getTerminology().getTermDefinitions().keySet().iterator().next();
        }
        if(archetype.getTerminology() != null && archetype.getTerminology().getValueSets() != null) {
            for(String code:archetype.getTerminology().getValueSets().keySet()) {
                if(!archetype.getTerminology().getTermDefinitions().get(language).containsKey(code) &&
                        AOMUtils.getSpecializationDepthFromCode(code) == archetype.specializationDepth()) {
                    Archetype flatParent = repo.getFlatArchetype(archetype.getParentArchetypeId());
                    createTermForNewCodeWithFlatParent(archetype, code, flatParent);
                }
                for(String valueCode:archetype.getTerminology().getValueSets().get(code).getMembers()) {
                    if(!archetype.getTerminology().getTermDefinitions().get(language).containsKey(valueCode) &&
                            AOMUtils.getSpecializationDepthFromCode(valueCode) == archetype.specializationDepth()) {
                        Archetype flatParent = repo.getFlatArchetype(archetype.getParentArchetypeId());
                        createTermForNewCodeWithFlatParent(archetype, valueCode, flatParent);
                    }
                }
            }
        }
    }

    private void addTerminologyIfNotPresent(Archetype archetype) {
        if(archetype.getTerminology() == null) {
            archetype.setTerminology(new ArchetypeTerminology());
        }
        if(archetype.getTerminology().getTermDefinitions().isEmpty()) {
            archetype.getTerminology().getTermDefinitions().put(originalLanguage, new LinkedHashMap<>());
        }
    }

    /** If any languages do not exist in the terminology, remove from translations*/
    private void removeUntranslatedLanguages(Archetype archetype) {
        if(archetype.getTranslations() == null) {
            return;
        }
        List<String> toRemove = new ArrayList<>();
        for(String translation:archetype.getTranslations().keySet()) {
            if(archetype.getTerminology() != null && !archetype.getTerminology().getTermDefinitions().containsKey(translation)) {
                toRemove.add(translation);
            }
        }
        if(!toRemove.isEmpty()) {
            for(String translation:toRemove) {
                archetype.getTranslations().remove(translation);
            }
        }
    }

    private void fixTerms(Archetype archetype, FlatArchetypeProvider repo, CComplexObject cObject) {
        String language = originalLanguage;
        if(!archetype.getTerminology().getTermDefinitions().containsKey(originalLanguage)) {
            language = archetype.getTerminology().getTermDefinitions().keySet().iterator().next();
        }
        if(cObject instanceof CArchetypeRoot) {
            if(!archetype.getTerminology().getTermDefinitions().get(language).containsKey(cObject.getNodeId()) &&
                    archetype.specializationDepth() == AOMUtils.getSpecializationDepthFromCode(cObject.getNodeId())
            ) {
                Archetype referencedArchetype = repo.getFlatArchetype(((CArchetypeRoot) cObject).getArchetypeRef());
                createTermForNewCodeWithRoot(archetype, cObject.getNodeId(), referencedArchetype);
                //TODO: fix lots of problems where node ids are set wrong, for example id2.1 to set the occurrences of id2 to {0} is a problem!
            }
        } else if(cObject instanceof CComplexObject) {

            if(!archetype.getTerminology().getTermDefinitions().get(language).containsKey(cObject.getNodeId()) &&
                    archetype.specializationDepth() == AOMUtils.getSpecializationDepthFromCode(cObject.getNodeId())
            ) {
                Archetype flatParent = repo.getFlatArchetype(archetype.getParentArchetypeId());
                createTermForNewCodeWithFlatParent(archetype, cObject.getNodeId(), flatParent);
                //TODO: fix lots of problems where node ids are set wrong, for example id2.1 to set the occurrences of id2 to {0} is a problem!
            }
        }
        for(CAttribute attribute:cObject.getAttributes()) {
            fixTerms(archetype, repo, attribute);
        }
    }

    private void fixTerms(Archetype archetype, FlatArchetypeProvider repo, CAttribute cAttribute) {

        for(CObject cObject:cAttribute.getChildren()) {
            if(cObject instanceof CComplexObject) {
                fixTerms(archetype, repo, (CComplexObject) cObject);
            }
        }
    }

    private static Pattern synthesizedCodesPattern = Pattern.compile("(id)(0\\.)*9[0-9][0-9][0-9](\\.[0-9]*)*");

    private void createTermForNewCodeWithRoot(Archetype archetype, String code, Archetype referencedArchetype) {
        if(!synthesizedCodesPattern.matcher(code).matches()) {
            //if(cObject.getParent().isMultiple()) {
            for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
                //TODO: add new archetype term to conversion log?

                ArchetypeTerm newTerm = new ArchetypeTerm();
                newTerm.setCode(code);
                ArchetypeTerm rootTerm = null;
                if (referencedArchetype != null) {
                    rootTerm = referencedArchetype.getTerm(referencedArchetype.getDefinition(), language);
                    if(rootTerm == null) {
                       rootTerm = referencedArchetype.getDefinition().getTerm();
                    }
                    if(rootTerm == null) {
                        //yeas this is persistent
                        rootTerm = referencedArchetype.getTerminology().getTermDefinition("en", "id1");
                    }
                }

                newTerm.setText(rootTerm == null ? "* missing code" : rootTerm.getText());
                newTerm.setDescription(rootTerm == null ? "* missing code" : rootTerm.getDescription());
                archetype.getTerminology().getTermDefinitions().get(language).put(newTerm.getCode(), newTerm);

            }
        }
       // }
    }

    private void createTermForNewCodeWithFlatParent(Archetype archetype, String code, Archetype flatParent) {
        if(!synthesizedCodesPattern.matcher(code).matches()) {
            //TODO: better would be, but difficult to do correctly:
            // if(cObject.getParent().isMultiple()) {
            for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
                //TODO: add new archetype term to conversion log?

                ArchetypeTerm newTerm = new ArchetypeTerm();
                newTerm.setCode(code);
                ArchetypeTerm parentTerm = null;
                if (flatParent != null) {
                    ArchetypeTerminology parentTerminology = flatParent.getTerminology();
                    if(parentTerminology != null) {
                        if(parentTerminology.getTermDefinitions().get(language) != null) {
                            parentTerm = parentTerminology.getTermDefinition(language, AOMUtils.codeAtLevel(code, flatParent.specializationDepth()));
                        } else {
                            parentTerm = parentTerminology.getTermDefinition(flatParent.getOriginalLanguage().getCodeString(), AOMUtils.codeAtLevel(code, flatParent.specializationDepth()));
                        }
                    }
                }

                newTerm.setText(parentTerm == null ? "* missing code" : parentTerm.getText());
                newTerm.setDescription(parentTerm == null ? "* missing code" : parentTerm.getDescription());
                archetype.getTerminology().getTermDefinitions().get(language).put(newTerm.getCode(), newTerm);

            }
        }
        // }
    }



}
