package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMTypeInfo;

import java.io.IOException;

/**
 * Class that handles naming of Archie RM and AOM objects for use in Jackson.
 *
 * The AOM class CComplexObject will get the type name "C_COMPLEX_OBJECT"
 * The RM class DvDateTime will get the type name "DV_DATE_TIME"
 */
public class ArchieTypeNameResolver extends ClassNameIdResolver {

    private ModelInfoLookup rmInfoLookup;
    private ModelInfoLookup aomInfoLookup;

    public ArchieTypeNameResolver(ModelInfoLookup rmInfoLookup, boolean standardsCompliantExpressionClassNames) {
        super(TypeFactory.defaultInstance().constructType(OpenEHRBase.class), TypeFactory.defaultInstance());
        aomInfoLookup = ArchieAOMInfoLookup.getInstance(standardsCompliantExpressionClassNames);
        this.rmInfoLookup = rmInfoLookup;
    }

    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    @Override
    public String idFromValue(Object value) {

        RMTypeInfo typeInfo = rmInfoLookup.getTypeInfo(value.getClass());
        if(typeInfo == null) {
            typeInfo = aomInfoLookup.getTypeInfo(value.getClass());
        }
        if(typeInfo != null) {
            //this case is faster and should always work. If for some reason it does not, the case below works fine instead.
            return typeInfo.getRmName();
        } else {
            return rmInfoLookup.getNamingStrategy().getTypeName(value.getClass());
        }
// This should work in all cases for openEHR-classes and this should not be used for other classes
// Additional code for making this work on non-ehr-types:
//        } else {
//            return super.idFromValue(value);
//        }
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        return _typeFromId(id, context);
    }

    @Override
    protected JavaType _typeFromId(String typeName, DatabindContext ctxt) throws IOException {
        Class<?> result = rmInfoLookup.getClass(typeName);
        if(result == null) {
            //AOM class?
            result = aomInfoLookup.getClass(typeName);
        }
        if(result != null) {
            TypeFactory typeFactory = (ctxt == null) ? _typeFactory : ctxt.getTypeFactory();
            return typeFactory.constructSpecializedType(_baseType, result);
        }
        return super._typeFromId(typeName, ctxt);
    }
}
