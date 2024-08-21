package org.s2.util.enumerations;

/// Integer-based enumeration type.
public class EnumerationTypeInteger extends EnumerationType<Integer> {
    public EnumerationTypeInteger(String[] itemNames, Integer[] itemValues) {
        super(EnumerationType.P_BMM_ENUMERATION_UNDERLYING_TYPE_INTEGER, itemNames, itemValues);
    }
}
