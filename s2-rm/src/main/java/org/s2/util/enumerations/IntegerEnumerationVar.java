package org.s2.util.enumerations;

public abstract class IntegerEnumerationVar<T extends EnumerationType<Integer>> extends EnumerationVar<Integer, T>  {
    public static Class<?> underlyingType()
    {
        return Integer.class;
    };

}
