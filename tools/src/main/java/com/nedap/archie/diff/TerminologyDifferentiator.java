package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;

import java.net.URI;
import java.util.*;

/**
 * Differentiates the terminology
 */
public class TerminologyDifferentiator {

    public void differentiate(Archetype result) {
        removeAdditionalTranslations(result);
        differentiateTermDefinitions(result);
        differentiateTermBindings(result);
        differentiateValueSets(result);
        //terminology extracts cannot appear in a differential archetype
        removeTerminologyExtracts(result);

    }

    private void removeAdditionalTranslations(Archetype result) {
        //a specialized archetype possibly only adds translations for some languages
        // so remove all from the terminology that have not been speciafically defined in the archetype resource description
        List<String> translationsToRemove = new ArrayList<>();
        for(String key: result.getTerminology().getTermDefinitions().keySet()) {
            if(!result.getTranslations().containsKey(key) && !result.getTerminology().getOriginalLanguage().equals(key)) {
                translationsToRemove.add(key);
            }
        }
        for(String key:translationsToRemove) {
            result.getTerminology().getTermDefinitions().remove(key);
        }
    }

    private void removeTerminologyExtracts(Archetype result) {
        result.getTerminology().getTerminologyExtracts().clear();
    }

    private void differentiateTermBindings(Archetype result) {
        Map<String, Map<String, URI>> termBindings = result.getTerminology().getTermBindings();

        Set<String> termBindingSetsToRemove = new HashSet<>();

        for(String bindingKey:termBindings.keySet()) {
            Map<String, URI> bindings = termBindings.get(bindingKey);
            Set<String> termBindingsToRemove = new HashSet<>();
            for(String termCode: bindings.keySet()) {
                if(AOMUtils.getSpecializationDepthFromCode(termCode) < result.specializationDepth()) {
                    termBindingsToRemove.add(termCode);
                }
            }
            for(String termCode:termBindingsToRemove) {
                bindings.remove(termCode);
            }
            if(bindings.isEmpty()) {
                termBindingSetsToRemove.add(bindingKey);
            }
        }
        for(String bindingKey:termBindingSetsToRemove) {
            termBindings.remove(bindingKey);
        }

    }

    private void differentiateValueSets(Archetype result) {
        Map<String, ValueSet> valueSets = result.getTerminology().getValueSets();
        Set<String> valueSetsToRemove = new HashSet<>();
        for(String valueSetCode: valueSets.keySet()) {
            if(AOMUtils.getSpecializationDepthFromCode(valueSetCode) < result.specializationDepth()) {
                valueSetsToRemove.add(valueSetCode);
            }
        }
        for(String valueSetCode: valueSetsToRemove) {
            valueSets.remove(valueSetCode);
        }

    }

    private void differentiateTermDefinitions(Archetype result) {
        Map<String, Map<String, ArchetypeTerm>> termDefinitions = result.getTerminology().getTermDefinitions();
        Set<String> languagesToRemove = new HashSet<>();
        for(String language: termDefinitions.keySet()) {
            Map<String, ArchetypeTerm> terms = termDefinitions.get(language);
            Set<String> termsToDelete = new HashSet<>();
            for(String termCode:terms.keySet()) {
                if(AOMUtils.getSpecializationDepthFromCode(termCode) < result.specializationDepth()) {
                    termsToDelete.add(termCode);
                }
            }
            for(String term:termsToDelete){
                terms.remove(term);
            }
            if(terms.isEmpty()) {
                languagesToRemove.add(language);
            }
        }
        for(String language:languagesToRemove) {
            termDefinitions.remove(language);
        }
    }
}
