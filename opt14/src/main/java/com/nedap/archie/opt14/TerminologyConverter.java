package com.nedap.archie.opt14;

import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.adl14.ADL14ConversionUtil;
import com.nedap.archie.adl14.aom14.TermBindingsList;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.rm.archetyped.Link;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TerminologyConverter {

    public static ArchetypeTerminology createTerminology(OPERATIONALTEMPLATE opt14, CARCHETYPEROOT definition,
                                                         ADL14ConversionConfiguration config) {
        ArchetypeTerminology terminology = new ArchetypeTerminology();
        String language = opt14.getLanguage().getCodeString();
        LinkedHashMap<String, ArchetypeTerm> terms = new LinkedHashMap<>();
        terminology.getTermDefinitions().put(language, terms);
        if(definition.getTermBindings() != null) {
            ADL14ConversionUtil conversionUtil = new ADL14ConversionUtil(config);
            for(TermBindingSet bindings14:definition.getTermBindings()) {

                ensureTermBindingKeyExists(terminology, bindings14.getTerminology());
                Map<String, URI> newBindings = terminology.getTermBindings().get(bindings14.getTerminology());

                for(TERMBINDINGITEM item14:bindings14.getItems()) {
                    try {
                        URI newBindingValue = conversionUtil.convertToUri(BaseTypesConverter.convert(item14.getValue()));
                        newBindings.put(item14.getCode(), newBindingValue);
                        //So this is an old path, will be converted later
                        //not inside te parser, obviously
                        //URIs need to be converted to even fit into the new model
                    } catch (URISyntaxException e) {
                        //TODO: add to conversion notes/messages/warnings
                        //logger.warn("error converting term binding to URI", e);
                    }

                }
            }
        }
        for(ARCHETYPETERM term14:definition.getTermDefinitions()) {
            ArchetypeTerm term = new ArchetypeTerm();
            term.setCode(term14.getCode());
            for(StringDictionaryItem item:term14.getItems()) {
                term.put(item.getId(), item.getValue());
            }
            terms.put(term14.getCode(), term);
        }
        return terminology;
    }


    private static void ensureTermBindingKeyExists(ArchetypeTerminology terminology, String key) {
        if(!terminology.getTermBindings().containsKey(key)) {
            terminology.getTermBindings().put(key, new LinkedHashMap<>());
        }
    }
}
