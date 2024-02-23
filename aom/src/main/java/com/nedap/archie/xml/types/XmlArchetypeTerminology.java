package com.nedap.archie.xml.types;

import com.nedap.archie.aom.terminology.ValueSet;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * XML representation of the Archetype Terminology, for use in JAXB.
 * Created by pieter.bos on 26/07/16.
 */
@XmlType(name="ARCHETYPE_TERMINOLOGY")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlArchetypeTerminology {

    @XmlAttribute(name="archetype_id")
    private String archetypeId;
    @XmlAttribute(name="is_differential")
    private Boolean differential;
    @XmlAttribute(name="original_language")
    private String originalLanguage;
    @XmlElement(name="concept_code")
    private String conceptCode;

    @XmlElement(name="term_definitions")
    private List<CodeDefinitionSet> termDefinitions = new ArrayList<>();
    @XmlElement(name="term_bindings")
    private List<TermBindingSet> termBindings = new ArrayList<>();
    @XmlElement(name="terminology_extracts")
    private List<CodeDefinitionSet> terminologyExtracts = new ArrayList<>();
    @XmlElement(name="value_sets")
    private List<ValueSet> valueSets = new ArrayList<>();

    public String getArchetypeId() {
        return archetypeId;
    }

    public void setArchetypeId(String archetypeId) {
        this.archetypeId = archetypeId;
    }

    public List<CodeDefinitionSet> getTermDefinitions() {
        return termDefinitions;
    }

    public void setTermDefinitions(List<CodeDefinitionSet> termDefinitions) {
        this.termDefinitions = termDefinitions;
    }

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

    public List<TermBindingSet> getTermBindings() {
        return termBindings;
    }

    public void setTermBindings(List<TermBindingSet> termBindings) {
        this.termBindings = termBindings;
    }

    public List<CodeDefinitionSet> getTerminologyExtracts() {
        return terminologyExtracts;
    }

    public void setTerminologyExtracts(List<CodeDefinitionSet> terminologyExtracts) {
        this.terminologyExtracts = terminologyExtracts;
    }

    public List<ValueSet> getValueSets() {
        return valueSets;
    }

    public void setValueSets(List<ValueSet> valueSets) {
        this.valueSets = valueSets;
    }
}
