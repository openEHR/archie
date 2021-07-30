package com.nedap.archie.opt14;

import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.adl14.ADL14ConversionUtil;
import com.nedap.archie.adl14.aom14.ArchetypeOntology;
import com.nedap.archie.adl14.aom14.ConstraintBindingsList;
import com.nedap.archie.adl14.aom14.TermBindingsList;
import com.nedap.archie.adl14.aom14.TermCodeList;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.rm.archetyped.Link;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.nedap.archie.opt14.schema.*;
import com.nedap.archie.rm.support.identification.TerminologyId;

class TerminologyConverter {

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
            ArchetypeTerm term = convertArchetypeTerm(term14);
            terms.put(term14.getCode(), term);
        }
        return terminology;
    }

    public static void convertOntologies(OPERATIONALTEMPLATE opt14, OperationalTemplate opt2, ADL14ConversionConfiguration config) {
        if(opt14.getOntology() != null) {
            ArchetypeTerminology terminology2 = opt2.getTerminology();
            if(terminology2 == null) {
                terminology2 = new ArchetypeTerminology();
                opt2.setTerminology(terminology2);
            }
            convertFlatTerminology(opt14, config, opt14.getOntology(), terminology2);
        }
        List<FLATARCHETYPEONTOLOGY> componentOntologies = opt14.getComponentOntologies();
        if(componentOntologies != null) {
            for(FLATARCHETYPEONTOLOGY flatarchetypeontology:componentOntologies) {
                ArchetypeTerminology terminology2 = opt2.getComponentTerminologies().get(flatarchetypeontology.getArchetypeId());
                if(terminology2 == null) {
                    terminology2 = new ArchetypeTerminology();
                    opt2.getComponentTerminologies().put(flatarchetypeontology.getArchetypeId(), terminology2);
                }
                convertFlatTerminology(opt14, config, flatarchetypeontology, terminology2);
            }
        }
    }

    private static void convertFlatTerminology(OPERATIONALTEMPLATE opt14, ADL14ConversionConfiguration config, FLATARCHETYPEONTOLOGY flatarchetypeontology, ArchetypeTerminology terminology2) {
        if(flatarchetypeontology.getTermBindings() != null) {
            ADL14ConversionUtil conversionUtil = new ADL14ConversionUtil(config);
            for(TermBindingSet bindings14:flatarchetypeontology.getTermBindings()) {

                ensureTermBindingKeyExists(terminology2, bindings14.getTerminology());
                Map<String, URI> newBindings = terminology2.getTermBindings().get(bindings14.getTerminology());

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
        for(CodeDefinitionSet definitionSet14:flatarchetypeontology.getTermDefinitions()) {
            String language = definitionSet14.getLanguage();
            if(language == null) {
                language = opt14.getLanguage().getCodeString();
            }

            Map<String, ArchetypeTerm> terms = terminology2.getTermDefinitions().get(language);
            if(terms == null) {
                terms = new LinkedHashMap<>();
                terminology2.getTermDefinitions().put(language, terms);
            }

            for(ARCHETYPETERM term14:definitionSet14.getItems()) {
                ArchetypeTerm term = convertArchetypeTerm(term14);
                terms.put(term14.getCode(), term);
            }
        }
        ADL14ConversionUtil conversionUtil = new ADL14ConversionUtil(config);
        convertConstraintBindings(flatarchetypeontology, terminology2, conversionUtil);
        convertConstraintDefinitions(flatarchetypeontology, terminology2);
    }

    private static ArchetypeTerm convertArchetypeTerm(ARCHETYPETERM term14) {
        ArchetypeTerm term = new ArchetypeTerm();
        term.setCode(term14.getCode());
        for (StringDictionaryItem item : term14.getItems()) {
            term.put(item.getId(), item.getValue());
        }
        return term;
    }

    private static void convertConstraintDefinitions(FLATARCHETYPEONTOLOGY ontology, ArchetypeTerminology terminology) {
        if(ontology.getConstraintDefinitions() != null) {
            for(CodeDefinitionSet constraintDefinitions:ontology.getConstraintDefinitions()) {
                String language = constraintDefinitions.getLanguage();
                if(terminology.getTermDefinitions().get(language) == null) {
                    terminology.getTermDefinitions().put(language, new LinkedHashMap<>());
                }
                for(ARCHETYPETERM term:constraintDefinitions.getItems()) {
                    ArchetypeTerm archetypeTerm = convertArchetypeTerm(term);
                    terminology.getTermDefinitions().get(language).put(term.getCode(), archetypeTerm);
                }
            }
        }
    }

    private static void convertConstraintBindings(FLATARCHETYPEONTOLOGY ontology, ArchetypeTerminology terminology, ADL14ConversionUtil conversionUtil) {
        if(ontology.getConstraintBindings() != null) {
            for(ConstraintBindingSet constraintBinding:ontology.getConstraintBindings()) {
                ensureTermBindingKeyExists(terminology, constraintBinding.getTerminology());
                for(CONSTRAINTBINDINGITEM item14:constraintBinding.getItems()) {
                    Map<String, URI> termBindings = terminology.getTermBindings().get(constraintBinding.getTerminology());
                    try {
                        URI newBindingValue = conversionUtil.convertToUri(TerminologyCode.createFromString(
                                constraintBinding.getTerminology(),
                                null,
                                item14.getValue()));
                        termBindings.put(item14.getCode(), newBindingValue);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private static void ensureTermBindingKeyExists(ArchetypeTerminology terminology, String key) {
        if(!terminology.getTermBindings().containsKey(key)) {
            terminology.getTermBindings().put(key, new LinkedHashMap<>());
        }
    }
}
