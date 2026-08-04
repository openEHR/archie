package org.openehr.bmm.v2.persistence.jackson3;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.TypeIdResolver;
import com.google.common.collect.ImmutableBiMap;
import com.nedap.archie.base.OpenEHRBase;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.persistence.BmmTypeNames;

import java.util.Objects;

/**
 * Jackson 3 port of {@link org.openehr.bmm.v2.persistence.jackson.BmmTypeNaming}.
 */
public class BmmTypeNaming3 implements TypeIdResolver {

    private JavaType _baseType;

    private final static ImmutableBiMap<String, Class<?>> classNaming = BmmTypeNames.classNaming;
    private final static ImmutableBiMap<Class<?>, String> inverseClassNaming = BmmTypeNames.inverseClassNaming;

    protected BmmTypeNaming3() {
    }

    @Override
    public void init(JavaType baseType) {
        _baseType = baseType;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    @Override
    public String idFromValue(DatabindContext context, Object value) {
        String result = inverseClassNaming.get(value.getClass());
        return Objects.requireNonNullElseGet(result, () -> value.getClass().getSimpleName());
    }

    @Override
    public String idFromValueAndType(DatabindContext context, Object value, Class<?> suggestedType) {
        return idFromValue(context, value);
    }

    @Override
    public String idFromBaseType(DatabindContext context) {
        if (_baseType != null) {
            return idFromValue(context, _baseType.getRawClass());
        }
        return "";
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        String classKey = BmmDefinitions.typeNameToClassKey(id);
        Class<?> result = classNaming.get(classKey);
        JavaType baseType = _baseType != null ? _baseType : context.getTypeFactory().constructType(OpenEHRBase.class);
        if (result != null) {
            return context.getTypeFactory().constructSpecializedType(baseType, result);
        }
        // Fall back to class name resolution
        try {
            Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(id);
            return context.getTypeFactory().constructSpecializedType(baseType, cls);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("No class found for BMM type id: " + id, e);
        }
    }

    @Override
    public String getDescForKnownTypeIds() {
        return null;
    }
}
