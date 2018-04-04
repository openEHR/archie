package com.nedap.archie.aom.terminology;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.utils.AOMUtils;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ARCHETYPE_TERMINOLOGY_TEST")
public class ArchetypeTerminology extends ArchetypeModelObject {

    private Boolean differential;
    @XmlElement(name="original_language")
    private String originalLanguage;
    @XmlElement(name="concept_code")
    private String conceptCode;
    @XmlTransient//TODO!
    //@XmlElement(name="term_definitions")
    private Map<String, Map<String, ArchetypeTerm>> termDefinitions = new ConcurrentHashMap<>();
    //@XmlElement(name="term_bindings")
    @XmlTransient//TODO!
    private Map<String, Map<String, URI>> termBindings = new ConcurrentHashMap<>();
    //@XmlElement(name="terminology_extracts")
    @XmlTransient//TODO!
    private Map<String, Map<String, ArchetypeTerm>> terminologyExtracts = new ConcurrentHashMap<>();
    @XmlElement(name="value_sets")
    private Map<String, ValueSet> valueSets = new ConcurrentHashMap<>();

    @JsonIgnore
    private transient Archetype ownerArchetype;

    public Boolean getDifferential() {
        return differential;
    }

    public void setDifferential(Boolean differential) {
        this.differential = differential;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    public Map<String, Map<String, ArchetypeTerm>> getTermDefinitions() {
        return termDefinitions;
    }

    public void setTermDefinitions(Map<String, Map<String, ArchetypeTerm>> termDefinitions) {
        this.termDefinitions = termDefinitions;
    }

    public Map<String, Map<String, URI>> getTermBindings() {
        return termBindings;
    }

    public void setTermBindings(Map<String, Map<String, URI>> termBindings) {
        this.termBindings = termBindings;
    }

    public Map<String, Map<String, ArchetypeTerm>> getTerminologyExtracts() {
        return terminologyExtracts;
    }

    public void setTerminologyExtracts(Map<String, Map<String, ArchetypeTerm>> terminologyExtracts) {
        this.terminologyExtracts = terminologyExtracts;
    }

    public Archetype getOwnerArchetype() {
        return ownerArchetype;
    }

    public void setOwnerArchetype(Archetype ownerArchetype) {
        this.ownerArchetype = ownerArchetype;
    }

    public Map<String, ValueSet> getValueSets() {
        return valueSets;
    }

    public void setValueSets(Map<String, ValueSet> valueSets) {
        this.valueSets = valueSets;
    }

    public ArchetypeTerm getTermDefinition(String language, String code) {
        Map<String, ArchetypeTerm> translated = termDefinitions.get(language);
        if(translated == null) {
            return null;
        }
        return translated.get(code);
    }

    public URI getTermBinding(String terminologyId, String code) {
        Map<String, URI> translated = termBindings.get(terminologyId);
        if(translated == null) {
            return null;
        }
        return translated.get(code);
    }

    public Integer specialisationDepth() {
        return AOMUtils.getSpecializationDepthFromCode(conceptCode);
    }

    public List<String> idCodes() {
        HashSet<String> codes = new HashSet<>();
        for(String language:getTermDefinitions().keySet()) {
            for(String code:getTermDefinitions().get(language).keySet()) {
                if(AOMUtils.isIdCode(code)) {
                    codes.add(code);
                }
            }
        }
        return new ArrayList<String>(codes);
    }

    public List<String> valueCodes() {
        HashSet<String> codes = new HashSet<>();
        for(String language:getTermDefinitions().keySet()) {
            for(String code:getTermDefinitions().get(language).keySet()) {
                if(AOMUtils.isValueCode(code)) {
                    codes.add(code);
                }
            }
        }
        return new ArrayList<>(codes);
    }

    public List<String> valueSetCodes() {
        HashSet<String> codes = new HashSet<>();
        for(String language:getTermDefinitions().keySet()) {
            for(String code:getTermDefinitions().get(language).keySet()) {
                if(AOMUtils.isValueSetCode(code)) {
                    codes.add(code);
                }
            }
        }
        return new ArrayList<>(codes);
    }

    public List<String> allCodes() {
        HashSet<String> codes = new HashSet<>();
        for(String language:getTermDefinitions().keySet()) {
            for(String code:getTermDefinitions().get(language).keySet()) {
                codes.add(code);
            }
        }
        return new ArrayList<>(codes);
    }

    public boolean hasCode(String code) {
        if(termDefinitions == null) {
            return false;
        }
        if(originalLanguage == null) {
            for(String language:getTermDefinitions().keySet()) {
                if(getTermDefinitions().get(language).containsKey(code)) {
                    return true;
                }
            }
            return false;
        } else {
            Map<String, ArchetypeTerm> termDefinitionsForLanguage = getTermDefinitions().get(originalLanguage);
            return termDefinitionsForLanguage != null && termDefinitionsForLanguage.containsKey(code);
        }
    }

    public boolean hasIdCode(String code) {
        return AOMUtils.isIdCode(code) && hasCode(code);
    }

    public boolean hasCodeInAllLanguages(String code) {
        if(termDefinitions == null) {
            return false;
        }
        for(String language:getTermDefinitions().keySet()) {
            if(!getTermDefinitions().get(language).containsKey(code)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasIdCodeInAllLanguages(String code) {
        return AOMUtils.isIdCode(code) && hasCodeInAllLanguages(code);
    }

    public boolean hasValueSetCode(String code) {
        return AOMUtils.isValueSetCode(code) && hasCode(code);
    }

    public boolean hasValueCode(String code) {
        return AOMUtils.isValueCode(code) && hasCode(code);
    }
}


