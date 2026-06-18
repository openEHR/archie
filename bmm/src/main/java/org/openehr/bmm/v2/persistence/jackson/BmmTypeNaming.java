package org.openehr.bmm.v2.persistence.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.ImmutableBiMap;
import com.nedap.archie.base.OpenEHRBase;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.persistence.BmmTypeNames;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that handles naming of Archie RM and AOM objects for use in Jackson.
 *
 * The AOM class CComplexObject will get the type name "C_COMPLEX_OBJECT"
 * The RM class DvDateTime will get the type name "DV_DATE_TIME"
 */
public class BmmTypeNaming extends ClassNameIdResolver {

    private final static ImmutableBiMap<String, Class<?>> classNaming = BmmTypeNames.classNaming;
    private final static ImmutableBiMap<Class<?>, String> inverseClassNaming = BmmTypeNames.inverseClassNaming;

    protected BmmTypeNaming() {
        super(TypeFactory.defaultInstance().constructType(OpenEHRBase.class), TypeFactory.defaultInstance());
    }

    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    @Override
    public String idFromValue(Object value) {
        String result = inverseClassNaming.get(value.getClass());
        //not sure if we need this. If so, it should implement naming such as ArchieNamingStrategy (requires module restructuring)
        return Objects.requireNonNullElseGet(result, () -> value.getClass().getSimpleName());
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
