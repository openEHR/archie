package com.nedap.archie.json3;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.TypeIdResolver;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMTypeInfo;

/**
 * Jackson 3 port of {@link com.nedap.archie.json.OpenEHRTypeNaming}.
 * Handles naming of Archie RM and AOM objects for use in Jackson 3.
 */
public class OpenEHRTypeNaming3 implements TypeIdResolver {

    private JavaType _baseType;
    private final ModelInfoLookup rmInfoLookup = ArchieRMInfoLookup.getInstance();
    private final ModelInfoLookup aomInfoLookup;

    protected OpenEHRTypeNaming3(boolean standardsCompliantExpressionClassNames) {
        aomInfoLookup = ArchieAOMInfoLookup.getInstance(standardsCompliantExpressionClassNames);
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
        RMTypeInfo typeInfo = rmInfoLookup.getTypeInfo(value.getClass());
        if (typeInfo == null) {
            typeInfo = aomInfoLookup.getTypeInfo(value.getClass());
        }
        if (typeInfo != null) {
            return typeInfo.getRmName();
        } else {
            return rmInfoLookup.getNamingStrategy().getTypeName(value.getClass());
        }
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
        Class<?> result = rmInfoLookup.getClass(id);
        if (result == null) {
            result = aomInfoLookup.getClass(id);
        }
        JavaType baseType = _baseType != null ? _baseType : context.getTypeFactory().constructType(OpenEHRBase.class);
        if (result != null) {
            return context.getTypeFactory().constructSpecializedType(baseType, result);
        }
        // Try to fall back to class name resolution
        try {
            Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(id);
            return context.getTypeFactory().constructSpecializedType(baseType, cls);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("No class found for OpenEHR type id: " + id, e);
        }
    }

    @Override
    public String getDescForKnownTypeIds() {
        return null;
    }
}
