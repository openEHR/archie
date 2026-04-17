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
import com.nedap.archie.aom.utils.ConformanceCheckResult;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.terminology.OpenEHRTerminologyAccess;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
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

    /** Temporary storage for multi-code ADL 1.4 constraints during conversion. Never serialized. */
    @JsonIgnore
    @XmlTransient
    private List<String> pendingCodes;

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
    public List<String> getPendingCodes() {
        return pendingCodes;
    }

    @JsonIgnore
    public void setPendingCodes(List<String> pendingCodes) {
        this.pendingCodes = pendingCodes;
    }

    @JsonIgnore
    public boolean isConstraintRequired() {
        return getEffectiveConstraintStatus() == ConstraintStatus.REQUIRED;
    }

    public ConstraintStatus getEffectiveConstraintStatus() {
        return constraintStatus == null ? ConstraintStatus.REQUIRED : constraintStatus;
    }

    @Override
    @Deprecated
    public boolean isValidValue(TerminologyCode value) {
        if(getConstraint() == null) {
            return true;
        }
        if(isConstraintRequired()) {
            if (value == null) return false;

            List<String> values;
            String terminologyId = value.getTerminologyId();
            if (terminologyId == null || terminologyId.equalsIgnoreCase("local") || AOMUtils.isValueSetCode(value.getTerminologyId())) {
                values = this.getValueSetExpanded();
            } else if (terminologyId.equalsIgnoreCase("openehr")) {
                values = this.getOpenEHRValueSetExpanded();
            } else {
                // This is not a local nor an openehr terminology.
                // If a term binding is there, we may be able to validate, if external, we wil not be able to.
                // Return true for now for non-local terminology values.
                //TODO: implement checking for direct term bindings later
                return !ValidationConfiguration.isFailOnUnknownTerminologyId();
            }

            if(values != null && !values.isEmpty()) {
                return value.getCodeString() != null && values.contains(value.getCodeString());
            }
        } else {
            return true;
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
    
    private List<String> getOpenEHRValueSetExpanded() {
        List<String> atCodes = getValueSetExpanded();
        ArchetypeTerminology terminology = getTerminology();
        OpenEHRTerminologyAccess terminologyAccess = OpenEHRTerminologyAccess.getInstance();
        List<String> result = new ArrayList<>();
        
        if(terminology == null) {
            return result;
        }
        
        for(String atCode : atCodes) {
            URI termBinding = terminology.getTermBinding("openehr", atCode);
            if (termBinding != null) {
                String code = terminologyAccess.parseTerminologyURI(termBinding.toString());
                if (code != null) {
                    result.add(code);
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
        List<String> valueSet = getValueSetExpanded();
        List<String> otherValueSet = otherCode.getValueSetExpanded();

        // A null constraint means unconstrained — an unconstrained parent accepts anything,
        // and an unconstrained child trivially conforms to any parent.
        if(otherCode.constraint == null) {
            return ConformanceCheckResult.conforms();
        }
        if(constraint == null) {
            return ConformanceCheckResult.conforms();
        }

        if(!getEffectiveConstraintStatus().cConformsTo(otherCode.getEffectiveConstraintStatus()) ) {
            //PROBLEM: if this child CTerminologyCode has no constraint status, it should override its parent.
            //it does not here!
            return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("specialized CTerminology code constraint status {0} is wider more than parent contraint status {1}", getEffectiveConstraintStatus(), otherCode.getEffectiveConstraintStatus()));
        }
        String thisConstraint = constraint;
        String otherConstraint = otherCode.constraint;
        Archetype archetype = this.getArchetype();
        if(AOMUtils.isValidValueSetCode(thisConstraint) && AOMUtils.isValidValueSetCode(otherConstraint)) {
            if (otherValueSet.isEmpty()) {
                return ConformanceCheckResult.conforms();
            }

            if(otherCode.isConstraintRequired()) {
                //if required, codes can be:
                // - reused directly
                // - specialized
                //this includes the value set codes
                if (!AOMUtils.codesConformant(thisConstraint, otherConstraint)) {
                    return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("child terminology constraint value set code {0} does not conform to parent constraint with value set code {1}", thisConstraint, otherConstraint));
                }
                for (String value : valueSet) {
                    if( !AOMUtils.valueSetContainsCodeOrParent(otherValueSet, value)) {
                        return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("child terminology constraint value code {0} is not contained in {1}, or a direct specialization of one of its values", value, otherValueSet));
                    }
                }
            } else {
                //if not required, everything goes
                return ConformanceCheckResult.conforms();
//                for (String value : valueSet) {
//                    if(valueSetContainsCodeOrSpecialization(otherValueSet, value) ||
//                            AOMUtils.getSpecialisationStatusFromCode(value, archetypeSpecialisationDepth) == CodeRedefinitionStatus.ADDED) {
//                        return false;
//                    }
//                }
            }
            return ConformanceCheckResult.conforms();
        } else {
            if(!AOMUtils.codesConformant(thisConstraint, otherConstraint)) {
                return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("child terminology constraint value code {0} does not conform to parent constraint with value code {1}", thisConstraint, otherConstraint));
            }
            return ConformanceCheckResult.conforms();
        }
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

}
