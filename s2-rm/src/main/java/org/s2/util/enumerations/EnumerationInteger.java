package org.s2.util.enumerations;

/// Integer-based enumeration type.
public class EnumerationInteger extends Enumeration<Integer> {
    public EnumerationInteger(String[] itemNames, Integer[] itemValues) {
        super(Enumeration.P_BMM_ENUMERATION_UNDERLYING_TYPE_INTEGER, itemNames, itemValues);
    }
}
