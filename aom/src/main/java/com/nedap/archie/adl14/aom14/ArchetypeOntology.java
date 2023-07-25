package com.nedap.archie.adl14.aom14;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArchetypeOntology {

    private List<String> terminologiesAvailable;
    //@XmlElement(name="term_definitions")
    private Map<String, TermCodeList> termDefinitions = new ConcurrentHashMap<>();
    private Map<String, TermCodeList> constraintDefinitions;
    private Map<String, TermBindingsList> termBindings = new ConcurrentHashMap<>();
    private Map<String, ConstraintBindingsList> constraintBindings;


    public List<String> getTerminologiesAvailable() {
        return terminologiesAvailable;
    }

    public void setTerminologiesAvailable(List<String> terminologiesAvailable) {
        this.terminologiesAvailable = terminologiesAvailable;
    }

    public Map<String, TermCodeList> getTermDefinitions() {
        return termDefinitions;
    }

    public void setTermDefinitions(Map<String, TermCodeList> termDefinitions) {
        this.termDefinitions = termDefinitions;
    }

    public Map<String, TermCodeList> getConstraintDefinitions() {
        return constraintDefinitions;
    }

    public void setConstraintDefinitions(Map<String, TermCodeList> constraintDefinitions) {
        this.constraintDefinitions = constraintDefinitions;
    }

    public Map<String, TermBindingsList> getTermBindings() {
        return termBindings;
    }

    public void setTermBindings(Map<String, TermBindingsList> termBindings) {
        this.termBindings = termBindings;
    }

    public Map<String, ConstraintBindingsList> getConstraintBindings() {
        return constraintBindings;
    }

    public void setConstraintBindings(Map<String, ConstraintBindingsList> constraintBindings) {
        this.constraintBindings = constraintBindings;
    }
}
