package com.nedap.archie.util;

import com.google.common.base.CaseFormat;

/**
 * Converts java names to openEHR names and vice versa
 *
 * Created by pieter.bos on 10/11/15.
 *
 * @deprecated This class is unused and will be removed.
 */
@Deprecated
public class NamingUtil {

    /**
     * @deprecated This class is unused and will be removed.
     */
    @Deprecated
    public static String typeIdToClassName(String typeId) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, typeId);
    }

    /**
     * @deprecated This class is unused and will be removed.
     */
    @Deprecated
    public static String classNameToTypeId(String className) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, className);
    }

    /**
     * @deprecated This class is unused and will be removed.
     */
    @Deprecated
    public static String attributeToField(String attributeName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, attributeName);
    }

    /**
     * @deprecated This class is unused and will be removed.
     */
    @Deprecated
    public static String fieldToAttributeName(String fieldName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }

    /**
     * @deprecated This class is unused and will be removed.
     */
    @Deprecated
    public static String attributeNameToTypeId(String attributeName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, attributeName);
    }

    /**
     * @deprecated This class is unused and will be removed.
     */
    @Deprecated
    public static String attributeNameToGetMethod(String snakeCased) {
        return "get" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, snakeCased);
    }

    /**
     * @deprecated This class is unused and will be removed.
     */
    @Deprecated
    public static String attributeNameToSetMethod(String snakeCased) {
        return "set" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, snakeCased);
    }
}
