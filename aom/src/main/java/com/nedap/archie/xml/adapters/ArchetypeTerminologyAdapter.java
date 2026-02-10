package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.xml.types.CodeDefinitionSet;
import com.nedap.archie.xml.types.StringDictionaryItem;
import com.nedap.archie.xml.types.TermBindingSet;
import com.nedap.archie.xml.types.XmlArchetypeTerminology;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converter to convert from the AOM model to something that can be serialized directly by JAXB into the format specified by the XSD
 *
 * Created by pieter.bos on 22/07/16.
 */
public class ArchetypeTerminologyAdapter extends XmlAdapter<XmlArchetypeTerminology, ArchetypeTerminology> {

    @Override
    public XmlArchetypeTerminology marshal(ArchetypeTerminology terminology) {
        if(terminology == null) {
            return null;
        }
        XmlArchetypeTerminology xmlTerminology = new XmlArchetypeTerminology();
        xmlTerminology.setOriginalLanguage(terminology.getOriginalLanguage());
        xmlTerminology.setDifferential(terminology.getDifferential());
        xmlTerminology.setConceptCode(terminology.getConceptCode());
        xmlTerminology.setTerminologyExtracts(convertIntoCodeDefinitionSetList(terminology.getTerminologyExtracts()));
        xmlTerminology.setTermDefinitions(convertIntoCodeDefinitionSetList(terminology.getTermDefinitions()));
        xmlTerminology.setTermBindings(convertIntoTermBindingSetList(terminology.getTermBindings()));
        xmlTerminology.setValueSets(convertIntoValueSetList(terminology.getValueSets()));
        if(terminology.getOwnerArchetype() != null && terminology.getOwnerArchetype().getArchetypeId() != null) {
            xmlTerminology.setArchetypeId(terminology.getOwnerArchetype().getArchetypeId().toString());
        }
        return xmlTerminology;
    }

    // Converters to JAXB Models to the AOM

    private List<ValueSet> convertIntoValueSetList(Map<String, ValueSet> valueSets) {
        return new ArrayList<>(valueSets.values());
    }


    private List<TermBindingSet> convertIntoTermBindingSetList(Map<String, Map<String, URI>> map) {
        List<TermBindingSet> result = new ArrayList<>();
        for(String id:map.keySet()) {
            Map<String, URI> terms = map.get(id);
            TermBindingSet termBindingSet = new TermBindingSet();
            termBindingSet.setId(id);
            ArrayList<StringDictionaryItem> items = StringDictionaryUtil.convertUriMapIntoStringDictionaryList(terms);
            termBindingSet.setItems(items);
            result.add(termBindingSet);
        }
        return result;
    }

    private List<CodeDefinitionSet> convertIntoCodeDefinitionSetList(Map<String, Map<String, ArchetypeTerm>> map) {
        List<CodeDefinitionSet> result = new ArrayList<>();
        for(String language:map.keySet()) {
            Map<String, ArchetypeTerm> terms = map.get(language);
            CodeDefinitionSet codeDefinitionSet = new CodeDefinitionSet();
            codeDefinitionSet.setLanguage(language);
            codeDefinitionSet.setItems(new ArrayList<>(terms.values()));
            result.add(codeDefinitionSet);
        }
        return result;
    }

    @Override
    public ArchetypeTerminology unmarshal(XmlArchetypeTerminology xmlTerminology) throws URISyntaxException {
        if(xmlTerminology == null) {
            return null;
        }
        ArchetypeTerminology terminology = new ArchetypeTerminology();
        terminology.setOriginalLanguage(xmlTerminology.getOriginalLanguage());
        terminology.setDifferential(xmlTerminology.getDifferential());
        terminology.setConceptCode(xmlTerminology.getConceptCode());
        terminology.setTerminologyExtracts(convertIntoArchetypeTermMap(xmlTerminology.getTerminologyExtracts()));
        terminology.setTermDefinitions(convertIntoArchetypeTermMap(xmlTerminology.getTermDefinitions()));
        terminology.setTermBindings(convertIntoTermBindingsMap(xmlTerminology.getTermBindings()));
        terminology.setValueSets(convertIntoValueSetMap(xmlTerminology.getValueSets()));
        return terminology;
    }


    // Converters to the AOM from JAXB models

    private Map<String, ValueSet> convertIntoValueSetMap(List<ValueSet> valueSets) {
        Map<String, ValueSet> result = new LinkedHashMap<>();
        for(ValueSet valueSet:valueSets) {
            result.put(valueSet.getId(), valueSet);
        }
        return result;
    }

    private Map<String, Map<String, URI>> convertIntoTermBindingsMap(List<TermBindingSet> termBindings) throws URISyntaxException {
        Map<String, Map<String, URI>> result = new LinkedHashMap<>();
        for(TermBindingSet set:termBindings) {
            Map<String, URI> termMap = StringDictionaryUtil.convertStringDictionaryListToUriMap(set.getItems());
            result.put(set.getId(), termMap);
        }
        return result;
    }



    private Map<String, Map<String, ArchetypeTerm>> convertIntoArchetypeTermMap(List<CodeDefinitionSet> list) {
        Map<String, Map<String, ArchetypeTerm>> result = new LinkedHashMap<>();
        for(CodeDefinitionSet set:list) {
            Map<String, ArchetypeTerm> termMap = new LinkedHashMap<>();
            for(ArchetypeTerm term:set.getItems()) {
                termMap.put(term.getCode(), term);
            }
            result.put(set.getLanguage(), termMap);
        }
        return result;
    }
}


