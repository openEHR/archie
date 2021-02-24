package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;

public class IncludedTerminology extends ArchetypeTerminology {

    public String key;

    public IncludedTerminology() {

    }

    public IncludedTerminology(String key, ArchetypeTerminology other) {
        this.key = key;
        this.setOriginalLanguage(other.getOriginalLanguage());
        this.setDifferential(other.getDifferential());
        this.setConceptCode(other.getConceptCode());
        this.setTerminologyExtracts(other.getTerminologyExtracts());
        this.setTermDefinitions(other.getTermDefinitions());
        this.setTermBindings(other.getTermBindings());
        this.setValueSets(other.getValueSets());
    }

    @JsonIgnore
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
