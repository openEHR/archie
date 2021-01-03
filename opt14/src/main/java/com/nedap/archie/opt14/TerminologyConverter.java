package com.nedap.archie.opt14;

import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;

import java.util.LinkedHashMap;

public class TerminologyConverter {

    public static ArchetypeTerminology createTerminology(OPERATIONALTEMPLATE opt14, CARCHETYPEROOT definition) {
        ArchetypeTerminology terminology = new ArchetypeTerminology();
        String language = opt14.getLanguage().getCodeString();
        LinkedHashMap<String, ArchetypeTerm> terms = new LinkedHashMap<>();
        terminology.getTermDefinitions().put(language, terms);
        for(ARCHETYPETERM term14:definition.getTermDefinitions()) {
            ArchetypeTerm term = new ArchetypeTerm();
            term.setCode(term14.getCode());
            for(StringDictionaryItem item:term14.getItems()) {
                term.put(item.getId(), item.getId());
            }
            terms.put(term14.getCode(), term);
        }
        //TODO: term bindings
        return terminology;
    }
}
