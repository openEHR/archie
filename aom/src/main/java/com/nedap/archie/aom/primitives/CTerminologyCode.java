package com.nedap.archie.aom.primitives;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.TerminologyCodeWithArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.ConformanceCheckResult;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.terminology.OpenEHRTerminologyAccess;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openehr.utils.message.I18n;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 *
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_TERMINOLOGY_CODE")
@XmlAccessorType(XmlAccessType.FIELD)
public class CTerminologyCode extends CPrimitiveObject<String, TerminologyCode> {

    @XmlElement(name="assumed_value")
    @Nullable
    private TerminologyCode assumedValue;
    private String constraint;

    @Nullable
    private ConstraintStatus constraintStatus;

    @Override
    public TerminologyCode getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(TerminologyCode assumedValue) {
        this.assumedValue = assumedValue;
    }

    @Override
    public String getConstraint() {
        return this.constraint;
    }

    @Override
    public List<String> getConstraintAsList() {
        return getConstraint() == null ? Collections.emptyList() : Collections.singletonList(getConstraint());
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public ConstraintStatus getConstraintStatus() {
        return constraintStatus;
    }

    public void setConstraintStatus(ConstraintStatus constraintStatus) {
        this.constraintStatus = constraintStatus;
    }

    @JsonIgnore
    public boolean isConstraintRequired() {
        return getEffectiveConstraintStatus() == ConstraintStatus.REQUIRED;
    }

    @JsonIgnore
    public ConstraintStatus getEffectiveConstraintStatus() {
        return constraintStatus == null ? ConstraintStatus.REQUIRED : constraintStatus;
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
        if(constraint != null) {
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

    private ArchetypeTerminology getTerminology() {
        Archetype archetype = getArchetype();
        if(archetype != null) {
            //ideally this would not happen, but no reference to archetype exists in leaf constraints in rules so far
            //so for now fix it so it doesn't throw a NullPointerException
            return archetype.getTerminology(this);
        }
        return null;
    }

    @JsonIgnore
    public List<String> getValueSetExpanded() {
        List<String> result = new ArrayList<>();
        ArchetypeTerminology terminology = getTerminology();
        if(constraint != null) {
            if(constraint.startsWith("at")) {
                result.add(constraint);
            } else if (constraint.startsWith("ac")) {
                if(terminology != null) {
                    ValueSet acValueSet = terminology.getValueSets().get(constraint);
                    if (acValueSet != null) {
                        result.addAll(AOMUtils.getExpandedValueSetMembers(terminology.getValueSets(), acValueSet));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public ConformanceCheckResult cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypesConformant) {
        ConformanceCheckResult superResult = super.cConformsTo(other, rmTypesConformant);
        if(!superResult.doesConform()) {
            return superResult;
        }
        //now guaranteed to be the same class
        CTerminologyCode otherCode = (CTerminologyCode) other;

        // An unconstrained parent accepts anything
        if(otherCode.isAnyAllowed()) {
            return ConformanceCheckResult.conforms();
        }

        // The constraint status can only be narrowed: example -> preferred -> extensible -> required
        if(!getEffectiveConstraintStatus().cConformsTo(otherCode.getEffectiveConstraintStatus())) {
            return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("specialized CTerminology code constraint status {0} is wider more than parent contraint status {1}", getEffectiveConstraintStatus(), otherCode.getEffectiveConstraintStatus()));
        }

        // If the parent constraint status is not required, the child automatically conforms
        if(!otherCode.isConstraintRequired()) {
            return ConformanceCheckResult.conforms();
        }

        // From here on, both parent and child constraint status are required
        String thisConstraint = constraint;
        String otherConstraint = otherCode.constraint;
        if(AOMUtils.isValidValueSetCode(thisConstraint) && AOMUtils.isValidValueSetCode(otherConstraint)) {
            List<String> otherValueSet = otherCode.getValueSetExpanded();
            if (otherValueSet.isEmpty()) {
                // an empty parent value set means there is no value set constraint, so the child conforms
                return ConformanceCheckResult.conforms();
            }
            //codes can be:
            // - reused directly
            // - specialized
            //this includes the value set codes
            if (!AOMUtils.codesConformant(thisConstraint, otherConstraint)) {
                return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("child terminology constraint value set code {0} does not conform to parent constraint with value set code {1}", thisConstraint, otherConstraint));
            }
            // deliberately more lenient than the specification, which requires each value to be in the parent
            // value set: specializations of the parent values are accepted as well
            for (String value : getValueSetExpanded()) {
                if( !AOMUtils.valueSetContainsCodeOrParent(otherValueSet, value)) {
                    return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("child terminology constraint value code {0} is not contained in {1}, or a direct specialization of one of its values", value, otherValueSet));
                }
            }
            return ConformanceCheckResult.conforms();
        } else {
            // an unconstrained child does not conform, because it is wider than the parent constraint
            if(!AOMUtils.codesConformant(thisConstraint, otherConstraint)) {
                return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("child terminology constraint value code {0} does not conform to parent constraint with value code {1}", thisConstraint, otherConstraint));
            }
            return ConformanceCheckResult.conforms();
        }
    }

    /**
     * @return true if this constraint does not constrain the code, so any code is allowed
     */
    @JsonIgnore
    public boolean isAnyAllowed() {
        return constraint == null || constraint.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{[");
        if(constraint != null) {
            result.append(constraint);
        }
        result.append("]}");
        return result.toString();
    }

    @Override
    public String getRmTypeName() {
        if (getParent() == null || getParent().getRmAttributeName() == null) {
            return "terminology_code";
        }
        switch (getParent().getRmAttributeName()) {
            case "defining_code":
                return "CODE_PHRASE";
            case "symbol":
                return "DV_CODED_TEXT";
            default:
                return "terminology_code";
        }
    }
}
