package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.COrdered;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.primitives.CTemporal;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.terminology.OpenEHRTerminologyAccess;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class PrimitiveObjectConstraintHelper {
    private final boolean failOnUnknownTerminologyId;

    PrimitiveObjectConstraintHelper(ValidationConfiguration validationConfiguration) {
        this.failOnUnknownTerminologyId = validationConfiguration.isFailOnUnknownTerminologyId();
    }

    /**
     * True if the given value is a valid value for this constraint
     * Must be overridden in classes where the AssumedAndDefaultValue is not the actual value.
     * For example when it is an interval or pattern
     *
     * @param value
     * @return
     */
    <ValueType> boolean isValidValue(CPrimitiveObject<?, ValueType> cPrimitiveObject, ValueType value) {
        if(cPrimitiveObject instanceof COrdered) {
            return isValidValue((COrdered<ValueType>) cPrimitiveObject, value);
        } else if(cPrimitiveObject instanceof CString) {
            return isValidValue((CString) cPrimitiveObject, (String) value);
        } else if(cPrimitiveObject instanceof CTerminologyCode) {
            return isValidValue((CTerminologyCode) cPrimitiveObject, (TerminologyCode) value);
        } else {
            return isValidValue_inner(cPrimitiveObject, value);
        }
    }

    private <ValueType> boolean isValidValue_inner(CPrimitiveObject<?, ValueType> cPrimitiveObject, ValueType value) {
        if(cPrimitiveObject.getConstraint().isEmpty()) {
            return true;
        }
        for(Object constraint:cPrimitiveObject.getConstraint()) {
            if(Objects.equals(constraint, value)) {
                return true;
            }
        }
        return false;
    }

    private <T> boolean isValidValue(COrdered<T> cOrdered, T value) {
        if(cOrdered instanceof CTemporal) {
            return isValidValue((CTemporal<T>) cOrdered, value);
        } else {
            return isValidValue_inner(cOrdered, value);
        }
    }

    private <T> boolean isValidValue_inner(COrdered<T> cOrdered, T value) {
        if(cOrdered.getConstraint().isEmpty()) {
            return true;
        }
        for(Interval<T> constraint:cOrdered.getConstraint()) {
            if(constraint.has(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidValue(CString cString, String value) {
        if(cString.getConstraint().isEmpty()) {
            return true;
        }
        for(String constraint:cString.getConstraint()) {
            if(constraint.length() > 1 &&
                    CString.isRegexConstraint(constraint)) {
                //regexp. Strip first and last character and match. If you want to input
                //data starting and ending with '/', you cannot in the AOM, although ADL lets you express if just fine.
                //perhaps we should make the constraint object something more expressive than a String?
                if(matchesRegexp(value, constraint)) {
                    return true;
                }
            } else {
                //TODO: does case matter here?
                if(Objects.equals(value, constraint)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesRegexp(String value, String constraint) {
        return value.matches(constraint.substring(1).substring(0, constraint.length()-2));
    }

    private <T> boolean isValidValue(CTemporal<T> cTemporal, T value) {
        if(cTemporal.getConstraint().isEmpty() && cTemporal.getPatternConstraint() == null) {
            return true;
        }
        if(cTemporal.getPatternConstraint() == null) {
            return isValidValue_inner(cTemporal, value);
        } else {
            //TODO: find a library that validates ISO 8601 patterns
            return true;
        }
    }

    private boolean isValidValue(CTerminologyCode terminologyCode, TerminologyCode value) {
        if(terminologyCode.getConstraint().isEmpty()) {
            return true;
        }
        if(terminologyCode.isConstraintRequired()) {
            if (value == null) return false;

            List<String> values;
            String terminologyId = value.getTerminologyId();
            if (terminologyId == null || terminologyId.equalsIgnoreCase("local") || AOMUtils.isValueSetCode(value.getTerminologyId())) {
                values = terminologyCode.getValueSetExpanded();
            } else if (terminologyId.equalsIgnoreCase("openehr")) {
                values = getOpenEHRValueSetExpanded(terminologyCode);
            } else if (terminologyId.equalsIgnoreCase("IANA_media-types")) {
                values = getIANAMediaTypesValueSetExpanded(terminologyCode);
            } else {
                // This is not a local nor an openehr terminology.
                // If a term binding is there, we may be able to validate, if external, we wil not be able to.
                // Return true for now for non-local terminology values.
                //TODO: implement checking for direct term bindings later
                return !failOnUnknownTerminologyId;
            }

            if(values != null && !values.isEmpty()) {
                return value.getCodeString() != null && values.contains(value.getCodeString());
            }
        } else {
            return true;
        }

        return false;
    }

    private List<String> getOpenEHRValueSetExpanded(CTerminologyCode terminologyCode) {
        List<String> atCodes = terminologyCode.getValueSetExpanded();
        ArchetypeTerminology terminology = getTerminology(terminologyCode);
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

    private List<String> getIANAMediaTypesValueSetExpanded(CTerminologyCode terminologyCode) {
        List<String> atCodes = terminologyCode.getValueSetExpanded();
        ArchetypeTerminology terminology = getTerminology(terminologyCode);
        OpenEHRTerminologyAccess terminologyAccess = OpenEHRTerminologyAccess.getInstance();
        List<String> result = new ArrayList<>();

        if(terminology == null) {
            return result;
        }

        for (String atCode : atCodes) {
            URI termBinding = terminology.getTermBinding("IANA_media-types", atCode);
            if (termBinding != null) {
                String value = terminologyAccess.parseIANATerminologyURI(termBinding.toString());
                if (value != null) {
                    result.add(value);
                }
            }
        }

        return result;
    }

    private ArchetypeTerminology getTerminology(CTerminologyCode cTerminologyCode) {
        Archetype archetype = cTerminologyCode.getArchetype();
        if(archetype != null) {
            //ideally this would not happen, but no reference to archetype exists in leaf constraints in rules so far
            //so for now fix it so it doesn't throw a NullPointerException
            return archetype.getTerminology(cTerminologyCode);
        }
        return null;
    }
}
