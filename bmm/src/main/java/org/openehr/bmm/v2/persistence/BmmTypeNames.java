package org.openehr.bmm.v2.persistence;

import com.google.common.collect.ImmutableBiMap;
import com.nedap.archie.base.Interval;

/**
 * Shared type name mappings used by both Jackson 2 and Jackson 3 type resolvers.
 */
public class BmmTypeNames {

    public static final ImmutableBiMap<String, Class<?>> classNaming = ImmutableBiMap.<String, Class<?>>builder()
            .put("BMM_INCLUDE_SPEC", BmmIncludeSpec.class)
            .put("P_BMM_CLASS", PBmmClass.class)
            .put("P_BMM_CONTAINER_PROPERTY", PBmmContainerProperty.class)
            .put("P_BMM_ENUMERATION", PBmmEnumeration.class)
            .put("P_BMM_ENUMERATION_STRING", PBmmEnumerationString.class)
            .put("P_BMM_ENUMERATION_INTEGER", PBmmEnumerationInteger.class)
            .put("P_BMM_GENERIC_PARAMETER", PBmmGenericParameter.class)
            .put("P_BMM_GENERIC_PROPERTY", PBmmGenericProperty.class)
            .put("P_BMM_GENERIC_TYPE", PBmmGenericType.class)
            .put("P_BMM_OPEN_TYPE", PBmmOpenType.class)
            .put("P_BMM_CONTAINER_TYPE", PBmmContainerType.class)
            .put("P_BMM_PACKAGE", PBmmPackage.class)
            .put("P_BMM_PROPERTY", PBmmProperty.class)
            .put("P_BMM_SCHEMA", PBmmSchema.class)
            .put("P_BMM_SIMPLE_TYPE", PBmmSimpleType.class)
            .put("P_BMM_SINGLE_PROPERTY", PBmmSingleProperty.class)
            .put("P_BMM_SINGLE_PROPERTY_OPEN", PBmmSinglePropertyOpen.class)
            .put("P_BMM_UNITARY_TYPE", PBmmUnitaryType.class)
            .put("P_BMM_TYPE", PBmmType.class)
            .put("INTERVAL", Interval.class)
            .build();

    public static final ImmutableBiMap<Class<?>, String> inverseClassNaming = classNaming.inverse();
}
