package org.s2.util.enumerations;

import com.nedap.archie.base.RMObject;

public abstract class StringEnumerationVar<T extends EnumerationType<String>> extends EnumerationVar<String, T>  {
    public static Class<?> underlyingType()
    {
        return String.class;
    };

}
