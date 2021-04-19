package com.nedap.archie.flattener;

import com.google.common.collect.Sets;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TerminologyFlattener {

    protected static void flattenTerminology(Archetype result, Archetype child) {

        ArchetypeTerminology resultTerminology = result.getTerminology();
        ArchetypeTerminology childTerminology = child.getTerminology();

        flattenTerminologyDefinitions(resultTerminology.getTermDefinitions(), childTerminology.getTermDefinitions());
        flattenTerminologyDefinitions(resultTerminology.getTerminologyExtracts(), childTerminology.getTerminologyExtracts());
        flattenTerminologyDefinitions(resultTerminology.getTermBindings(), childTerminology.getTermBindings());
        resultTerminology.setDifferential(false);//TODO: correct?

        Map<String, ValueSet> childValueSets = childTerminology.getValueSets();
        Map<String, ValueSet> resultValueSets = resultTerminology.getValueSets();

        flattenValueSets(childValueSets, resultValueSets);
    }

    /**
     * Gets a term definition for a nodeId. If a direct match is not found, try to find for matching node ids - so looking for id1.1.1 will then find id1.1 if id1.1.1 is not defined
     * @param termDefinitions
     * @param language
     * @param nodeId
     * @return
     */
    protected static ArchetypeTerm getTerm(Map<String, Map<String, ArchetypeTerm>> termDefinitions, String language, String nodeId) {
        Map<String, ArchetypeTerm> translations = termDefinitions.get(language);
        ArchetypeTerm term = translations.get(nodeId);
        Map<String, ArchetypeTerm> foundTerms = new LinkedHashMap<>();
        if(term == null) {
            for(Map.Entry<String, ArchetypeTerm> entry: translations.entrySet()) {
                if(nodeId.startsWith(entry.getKey() + ".")) {
                    foundTerms.put(nodeId, entry.getValue());
                }
            }
            //get the longest matching node id
            List<String> foundNodeIds = new ArrayList<>(foundTerms.keySet());
            Collections.sort(foundNodeIds, Comparator.comparingInt((id) -> id.length()));
            return foundTerms.get(foundNodeIds.get(foundNodeIds.size()-1));

        }
        return term;
    }

    private static void flattenValueSets(Map<String, ValueSet> childValueSets, Map<String, ValueSet> resultValueSets) {
        for(String key:childValueSets.keySet()) {
            ValueSet childValueSet = childValueSets.get(key);
            ValueSet overriddenValueSet = findMatchingValueSet(resultValueSets, key);
            if(overriddenValueSet == null) {
                resultValueSets.put(key, childValueSet);
            } else {
                String originalId = overriddenValueSet.getId();
                overriddenValueSet.setId(childValueSet.getId());
                //we could just set the overridden value set to the new value and remove the old one
                //but that would mean you could also add codes in a specialized archetype- and you cannot
                Set<String> newMembers = new LinkedHashSet<>();
                for(String member: overriddenValueSet.getMembers()) {
                    if(childValueSet.getMembers().contains(member)) {
                        //can only delete, not add members.
                        newMembers.add(member);
                    }
                }
                overriddenValueSet.setMembers(newMembers);
                resultValueSets.remove(originalId);
                resultValueSets.put(key, overriddenValueSet);
            }
        }
    }

    private static ValueSet findMatchingValueSet(Map<String, ValueSet> resultValueSets, String specializedId) {
        return resultValueSets.values().stream()
                .filter((valueSet) -> AOMUtils.isOverriddenIdCode(specializedId, valueSet.getId()))
                .findAny().orElse(null);
    }

    private static <T> void flattenTerminologyDefinitions(Map<String, Map<String, T>> resultTermDefinitions, Map<String, Map<String, T>> childTermDefinitions) {
        for(String language:childTermDefinitions.keySet()) {
            if(!resultTermDefinitions.containsKey(language)) {
                resultTermDefinitions.put(language, childTermDefinitions.get(language));
            } else {
                for(String nodeId:childTermDefinitions.get(language).keySet()) {
                    resultTermDefinitions.get(language)
                            .put(nodeId, childTermDefinitions.get(language).get(nodeId));
                }
            }
        }
    }


    /* OPT CREATION */

    /**
     * Remove all languages from an archetype terminology unless listed in languages to keep.
     * @param result
     */
    protected static void filterLanguages(OperationalTemplate result, boolean removeLanguagesFromMetaData, String[] languagesToKeep) {
        if(languagesToKeep != null) {

            Set<String> languagesSet = Sets.newHashSet(languagesToKeep);

            filterLanguages(languagesSet, result.getTerminology());
            for(ArchetypeTerminology terminology: result.getComponentTerminologies().values()) {
                filterLanguages(languagesSet, terminology);
            }
            for(ArchetypeTerminology terminology: result.getTerminologyExtracts().values()) {
                filterLanguages(languagesSet, terminology);
            }

            if(removeLanguagesFromMetaData) {
                if(result.getDescription() != null) {
                    filterLanguages(languagesSet, result.getDescription().getDetails());
                }
                if(result.getTranslations() != null) {
                    filterLanguages(languagesSet, result.getTranslations());
                }
            }
        }
    }

    private static void filterLanguages(Set<String> languagesSet, ArchetypeTerminology terminology) {

        filterLanguages(languagesSet, terminology.getTermDefinitions());
        filterLanguages(languagesSet, terminology.getTerminologyExtracts());
    }

    private static void filterLanguages(Set<String> languagesSet, Map<String, ?> termDefinitions) {
        if(termDefinitions == null) {
            return;
        }
        List<String> toRemove = new ArrayList<>();
        for(String key: termDefinitions.keySet()) {
            if(!languagesSet.contains(key)) {
                toRemove.add(key);
            }
        }
        for(String key:toRemove) {
            termDefinitions.remove(key);
        }
    }

}
