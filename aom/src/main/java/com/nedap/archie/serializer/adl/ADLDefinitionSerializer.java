package com.nedap.archie.serializer.adl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.rminfo.RMObjectMapperProvider;
import com.nedap.archie.serializer.adl.constraints.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * @author markopi
 */
public class ADLDefinitionSerializer {
    protected final ADLStringBuilder builder;

    private final Map<Class, ConstraintSerializer> constraintSerializers;
    private Function<String, Archetype> flatArchetypeProvider;
    private RMObjectMapperProvider rmObjectMapperProvider;

    public ADLDefinitionSerializer(ADLStringBuilder builder, Function<String, Archetype> flatArchetypeProvider, RMObjectMapperProvider rmObjectMapperProvider) {
        this.builder = builder;
        this.flatArchetypeProvider = flatArchetypeProvider;
        this.rmObjectMapperProvider = rmObjectMapperProvider;

        constraintSerializers = new HashMap<>();
        constraintSerializers.put(ArchetypeSlot.class, new ArchetypeSlotSerializer(this));
        constraintSerializers.put(CArchetypeRoot.class, new CArchetypeRootSerializer(this));
        constraintSerializers.put(CBoolean.class, new CBooleanSerializer(this));
        constraintSerializers.put(CComplexObject.class, new CComplexObjectSerializer(this));
        constraintSerializers.put(CComplexObjectProxy.class, new CComplexObjectProxySerializer(this));
        constraintSerializers.put(CDate.class, new CDateSerializer(this));
        constraintSerializers.put(CDateTime.class, new CDateTimeSerializer(this));
        constraintSerializers.put(CDuration.class, new CDurationSerializer(this));
        constraintSerializers.put(CInteger.class, new CIntegerSerializer(this));
        constraintSerializers.put(CReal.class, new CRealSerializer(this));
        constraintSerializers.put(CString.class, new CStringSerializer(this));
        constraintSerializers.put(CTerminologyCode.class, new CTerminologyCodeSerializer(this));
        constraintSerializers.put(CTime.class, new CTimeSerializer(this));
    }

    public static String serialize(CObject cons) {
        final ADLStringBuilder builder = new ADLStringBuilder();
        ADLDefinitionSerializer serializer = new ADLDefinitionSerializer(builder, null, null);
        serializer.appendCObject(cons);
        return builder.toString();
    }

    public ADLStringBuilder getBuilder() {
        return builder;
    }

    public String getTermText(CObject cobj) {
        return getTermText(cobj, null);
    }

    public String getTermText(CObject cobj, String code) {
        if(cobj == null) {
            return null;
        }
        Archetype archetype = cobj.getArchetype();
        String originalLanguage = ofNullable(archetype)
                .flatMap(a -> ofNullable(a.getOriginalLanguage()))
                .map(TerminologyCode::getCodeString)
                .orElse(null);
        if (originalLanguage == null) return null;


        ArchetypeTerm term = code == null ?
                cobj.getArchetype().getTerm(cobj, originalLanguage) :
                cobj.getArchetype().getTerm(cobj, code, originalLanguage);
        if (term == null) {
            if(flatArchetypeProvider != null && archetype.getParentArchetypeId() != null) {
                Archetype flatParent = flatArchetypeProvider.apply(archetype.getParentArchetypeId());
                if(flatParent != null && flatParent.getTerminology() != null) {
                    ArchetypeTerminology terminology = flatParent.getTerminology();
                    String nodeId = AOMUtils.codeAtLevel(cobj.getNodeId(), flatParent.specializationDepth());
                    term = terminology.getTermDefinition(originalLanguage, nodeId);
                }
            }

        }
        if(term == null) {
            return null;
        }
        return term.getText();
    }

    public void appendCObject(CObject cobj) {
        ConstraintSerializer<CObject> serializer = getSerializer(cobj);
        if (serializer != null) {
            serializer.serialize(cobj);
        } else {
            throw new AssertionError("Unsupported constraint: " + cobj.getClass().getName());
        }
    }

    public boolean isEmpty(CObject cobj) {
        ConstraintSerializer<CObject> serializer = getSerializer(cobj);
        return serializer != null && serializer.isEmpty(cobj);
    }

    public String getSimpleCommentText(CObject cobj) {
        ConstraintSerializer<CObject> serializer = getSerializer(cobj);
        if (serializer == null) return null;
        return serializer.getSimpleCommentText(cobj);
    }

    @SuppressWarnings("unchecked")
    private ConstraintSerializer<CObject> getSerializer(CObject cobj) {
        Class c = cobj.getClass();
        ConstraintSerializer result = constraintSerializers.get(c);
        while(result == null && c.getSuperclass() != null) {
            c = c.getSuperclass();
            result = constraintSerializers.get(c);
        }
        return result;
    }

    public RMObjectMapperProvider getRmObjectMapperProvider() {
        return rmObjectMapperProvider;
    }
}
