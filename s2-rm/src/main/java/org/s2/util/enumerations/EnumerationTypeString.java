package org.s2.util.enumerations;

/// String-based enumeration type.
public class EnumerationTypeString extends EnumerationType<String> {
    public EnumerationTypeString(String[] itemNames, String[] itemValues) {
        super(EnumerationType.P_BMM_ENUMERATION_UNDERLYING_TYPE_STRING, itemNames, itemValues);
    }
}
