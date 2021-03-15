package com.nedap.archie.aom.primitives;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.ValidationConfiguration;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.TerminologyCodeWithArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.rminfo.ModelInfoLookup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 *
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_TERMINOLOGY_CODE")
@XmlAccessorType(XmlAccessType.FIELD)
public class CTerminologyCode extends CPrimitiveObject<String, TerminologyCode> {

    @XmlElement(name="assumed_value")
    private TerminologyCode assumedValue;
    private List<String> constraint = new ArrayList<>();

    @Override
    public TerminologyCode getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(TerminologyCode assumedValue) {
        this.assumedValue = assumedValue;
    }

    @Override
    public List<String> getConstraint() {
        return this.constraint;
    }

    @Override
    public void setConstraint(List<String> constraint) {
        this.constraint = constraint;
    }

    @Override
    public void addConstraint(String constraint) {
        this.constraint.add(constraint);
    }

    @Override
    public boolean isValidValue(TerminologyCode value) {
        if(getConstraint().isEmpty()) {
            return true;
        }
        if(value != null && value.getTerminologyId() != null && !value.getTerminologyId().equalsIgnoreCase("local") && !AOMUtils.isValueSetCode(value.getTerminologyId())) {
            //this is a non-local terminology. If a term binding is there, we may be able to validate, if external, we wil not be able to
            //so return true for now for non-local terminology values
            //TODO: implement checking for direct term bindings later
            //TODO: implement checking for include openehr-terminology
            return !ValidationConfiguration.isFailOnUnknownTerminologyId();
        }
        List<String> values = this.getValueSetExpanded();
        if(values != null && !values.isEmpty()) {
            return value.getCodeString() != null && values.contains(value.getCodeString());
        }

        return false;
    }


    /**
     * Get the ArchetypeTerms in the selected meaning and description language for all the possible options if this is a
     * locally defined terminology.
     * See the ArchieLanguageConfiguration for the language settings.
     *
     * @return
     */
    public List<TerminologyCodeWithArchetypeTerm> getTerms() {
        List<TerminologyCodeWithArchetypeTerm> result = new ArrayList<>();
        Archetype archetype = getArchetype();
        if(archetype == null) {
            //ideally this would not happen, but no reference to archetype exists in leaf constraints in rules so far
            //so for now fix it so it doesn't throw a NullPointerException
            return result;
        }
        ArchetypeTerminology terminology = archetype.getTerminology(this);
        String language = ArchieLanguageConfiguration.getMeaningAndDescriptionLanguage();
        String defaultLanguage = ArchieLanguageConfiguration.getDefaultMeaningAndDescriptionLanguage();
        for(String constraint:getConstraint()) {
            if(constraint.startsWith("at")) {
                ArchetypeTerm termDefinition = terminology.getTermDefinition(language, constraint);
                if(termDefinition == null) {
                    termDefinition = terminology.getTermDefinition(defaultLanguage, constraint);
                }
                if(termDefinition != null) {
                    result.add(new TerminologyCodeWithArchetypeTerm(constraint, termDefinition));
                }
            } else if (constraint.startsWith("ac")) {
                ValueSet acValueSet = terminology.getValueSets().get(constraint);
                if(acValueSet != null) {
                    for(String atCode:acValueSet.getMembers()) {
                        ArchetypeTerm termDefinition = terminology.getTermDefinition(language, atCode);
                        if(termDefinition == null) {
                            termDefinition = terminology.getTermDefinition(defaultLanguage, atCode);
                        }
                        if(termDefinition != null) {
                            result.add(new TerminologyCodeWithArchetypeTerm(atCode, termDefinition));
                        }
                    }
                }
            }
        }
        return result;
    }

    private void setTerms(List<TerminologyCodeWithArchetypeTerm> terms) {
        //hack for jackson to work
    }

    @JsonIgnore
    public List<String> getValueSetExpanded() {
        List<String> result = new ArrayList<>();
        Archetype archetype = getArchetype();
        if(archetype == null) {
            //ideally this would not happen, but no reference to archetype exists in leaf constraints in rules so far
            //so for now fix it so it doesn't throw a NullPointerException
            return result;
        }
        ArchetypeTerminology terminology = archetype.getTerminology(this);
        for(String constraint:getConstraint()) {
            if(constraint.startsWith("at")) {
                result.add(constraint);
            } else if (constraint.startsWith("ac")) {
                ValueSet acValueSet = terminology.getValueSets().get(constraint);
                if(acValueSet != null) {
                    result.addAll(acValueSet.getMembers());
                }
            }
        }
        return result;
    }

    @Override
    public boolean cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypesConformant) {
        if(!super.cConformsTo(other, rmTypesConformant)) {
            return false;
        }
        //now guaranteed to be the same class
        CTerminologyCode otherCode = (CTerminologyCode) other;
        List<String> valueSet = getValueSetExpanded();
        List<String> otherValueSet = otherCode.getValueSetExpanded();
        if(constraint.size() != 1 || otherCode.constraint.size() != 1) {
            return false;//this is invalid in the RM
        }
        String thisConstraint = constraint.get(0);
        String otherConstraint = otherCode.constraint.get(0);
        if(AOMUtils.isValidValueSetCode(thisConstraint) && AOMUtils.isValidValueSetCode(otherConstraint)) {
            if (otherValueSet.isEmpty()) {
                return true;
            }
            if(!AOMUtils.codesConformant(thisConstraint, otherConstraint)) {
                return false;
            }
            for (String value : valueSet) {
                //TODO: redefine validation to actually work here!
                if (!otherValueSet.contains(value)) {
                    return false;
                }
            }
            return true;
        } else {
            return AOMUtils.codesConformant(thisConstraint, otherConstraint);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{[");
        boolean first = true;
        for(String constraint:getConstraint()) {
            if(!first) {
                result.append(", ");
            }
            first = false;
            result.append(constraint.toString());
        }
        result.append("]}");
        return result.toString();
    }

}
