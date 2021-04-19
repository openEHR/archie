package org.openehr.bmm.v2.persistence.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.ImmutableBiMap;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.OpenEHRBase;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.persistence.*;

import java.io.IOException;

/**
 * Class that handles naming of Archie RM and AOM objects for use in Jackson.
 *
 * The AOM class CComplexObject will get the type name "C_COMPLEX_OBJECT"
 * The RM class DvDateTime will get the type name "DV_DATE_TIME"
 */
public class BmmTypeNaming extends ClassNameIdResolver {

    private final static ImmutableBiMap<String, Class<?>> classNaming = ImmutableBiMap.<String, Class<?>>builder().
        put("BMM_INCLUDE_SPEC", BmmIncludeSpec.class).
        put("P_BMM_CLASS", PBmmClass.class).
        put("P_BMM_CONTAINER_PROPERTY", PBmmContainerProperty.class).
        put("P_BMM_ENUMERATION", PBmmEnumeration.class).
        put("P_BMM_ENUMERATION_STRING", PBmmEnumerationString.class).
        put("P_BMM_ENUMERATION_INTEGER", PBmmEnumerationInteger.class).
        put("P_BMM_GENERIC_PARAMETER", PBmmGenericParameter.class).
        put("P_BMM_GENERIC_PROPERTY", PBmmGenericProperty.class).
        put("P_BMM_GENERIC_TYPE", PBmmGenericType.class).
        put("P_BMM_OPEN_TYPE", PBmmOpenType.class).
        put("P_BMM_CONTAINER_TYPE", PBmmContainerType.class).
        put("P_BMM_PACKAGE", PBmmPackage.class).
        put("P_BMM_PROPERTY", PBmmProperty.class).
        put("P_BMM_SCHEMA", PBmmSchema.class).
        put("P_BMM_SIMPLE_TYPE", PBmmSimpleType.class).
        put("P_BMM_SINGLE_PROPERTY", PBmmSingleProperty.class).
        put("P_BMM_SINGLE_PROPERTY_OPEN", PBmmSinglePropertyOpen.class).
        put("P_BMM_UNITARY_TYPE", PBmmUnitaryType.class).
        put("P_BMM_TYPE", PBmmType.class).
        put("INTERVAL", Interval.class).build();

    private final static ImmutableBiMap<Class<?>, String>  inverseClassNaming = classNaming.inverse();

    protected BmmTypeNaming() {
        super(TypeFactory.defaultInstance().constructType(OpenEHRBase.class), TypeFactory.defaultInstance());
    }

    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    @Override
    public String idFromValue(Object value) {
        String result = inverseClassNaming.get(value.getClass());
        if (result != null) {
            return result;
        } else {
            //not sure if we need this. If so, it should implement naming such as ArchieNamingStrategy (requires module restructuring)
            return value.getClass().getSimpleName();
        }
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        return _typeFromId(id, context);
    }

    @Override
    protected JavaType _typeFromId(String typeName, DatabindContext ctxt) throws IOException {
        String classKey = BmmDefinitions.typeNameToClassKey(typeName);
        Class<?> result =  classNaming.get(classKey);
        if (result != null) {
            TypeFactory typeFactory = (ctxt == null) ? _typeFactory : ctxt.getTypeFactory();
            return typeFactory.constructSpecializedType(_baseType, result);
        }
        return super._typeFromId(typeName, ctxt);
    }
}
