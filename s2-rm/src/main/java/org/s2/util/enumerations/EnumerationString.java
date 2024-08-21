package org.s2.util.enumerations;

/// String-based enumeration type.
public class EnumerationString extends Enumeration<String> {
    public EnumerationString(String[] itemNames, String[] itemValues) {
        super(Enumeration.P_BMM_ENUMERATION_UNDERLYING_TYPE_STRING, itemNames, itemValues);
    }
}
