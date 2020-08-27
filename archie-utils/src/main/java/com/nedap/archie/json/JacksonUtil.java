package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nedap.archie.base.OpenEHRBase;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.support.identification.ArchetypeID;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.RMTypeInfo;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to obtain an ObjectMapper that works with both archie RM and AOM objects, serializing into
 * a JSON with openEHR-spec type names.
 *
 * When a standard is agreed upon in the openEHR-specs, this format will likely change.
 *
 * Created by pieter.bos on 30/06/16.
 */
public class JacksonUtil {

    //threadsafe, can be cached
    private static final ConcurrentHashMap<RMJacksonConfiguration, ObjectMapper> objectMapperByConfiguration = new ConcurrentHashMap<>();

    private static final String DEFAULT_TYPE_PARAMETER = "@type";

    /**
     * Get an object mapper that works with Archie RM and AOM objects. It will be cached in a static variable for
     * performance reasons
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        return getObjectMapper(new RMJacksonConfiguration());
    }

    public static ObjectMapper getObjectMapper(RMJacksonConfiguration configuration) {
        ObjectMapper objectMapper = objectMapperByConfiguration.get(configuration);
        if(objectMapper == null) {
            objectMapper = new ObjectMapper();
            configureObjectMapper(objectMapper, configuration);
            objectMapperByConfiguration.put(configuration, objectMapper);

        }
        return objectMapper;
    }

    /**
     * Configure an existing object mapper to work with Archie RM and AOM Objects.
     * Indentation is enabled. Feel free to disable again in your own code.
     * @param objectMapper
     */
    public static void configureObjectMapper(ObjectMapper objectMapper) {
        configureObjectMapper(objectMapper, new RMJacksonConfiguration());
    }

    public static void configureObjectMapper(ObjectMapper objectMapper, RMJacksonConfiguration configuration) {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        if(!configuration.isSerializeEmptyCollections()) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        }
        if(configuration.isFailOnUnknownProperties()) {
            objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        } else {
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }

        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule module = new SimpleModule();
        if(!configuration.isAddExtraFieldsInArchetypeId()) {
            module.setMixInAnnotation(ArchetypeID.class, FixArchetypeIDMixin.class);
        }

        if(!configuration.isAddPathProperty()) {
            module.setMixInAnnotation(Pathable.class, DontSerializePathMixin.class);
        }
        if(!configuration.isAddPathProperty() || !configuration.isAddExtraFieldsInArchetypeId()) {
            objectMapper.registerModule(module);
        }

        objectMapper.enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL);

        TypeResolverBuilder typeResolverBuilder = new ArchieTypeResolverBuilder(configuration)
                .init(JsonTypeInfo.Id.NAME, new OpenEHRTypeNaming())
                .typeProperty(configuration.getTypePropertyName())
                .typeIdVisibility(true)
                .inclusion(JsonTypeInfo.As.PROPERTY);

        //@type is always allowed as an extra property, even if we don't expect it.
        objectMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                if (propertyName.equalsIgnoreCase(configuration.getTypePropertyName())) {
                    return true;
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });

        objectMapper.setDefaultTyping(typeResolverBuilder);

    }

    /**
     * TypeResolverBuilder that outputs type information for all RMObject classes, but not for java classes.
     * Otherwise, you get this for an arrayList: "ARRAY_LIST: []", while you would expect "[]" without type
     */
    static class ArchieTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder
    {

        private Set<Class> classesToNotAddTypeProperty;
        public ArchieTypeResolverBuilder(RMJacksonConfiguration configuration)
        {
            super(ObjectMapper.DefaultTyping.NON_FINAL, BasicPolymorphicTypeValidator.builder()
                    .allowIfBaseType(OpenEHRBase.class).build());
            classesToNotAddTypeProperty = new HashSet<>();
            if(!configuration.isAlwaysIncludeTypeProperty()) {
                List<RMTypeInfo> allTypes = ArchieRMInfoLookup.getInstance().getAllTypes();
                for(RMTypeInfo type:allTypes) {
                    if(type.getDirectDescendantClasses().isEmpty()) {
                        classesToNotAddTypeProperty.add(type.getJavaClass());
                    }
                }
            }
        }

        @Override
        public boolean useForType(JavaType t)
        {
            return (OpenEHRBase.class.isAssignableFrom(t.getRawClass()) && !classesToNotAddTypeProperty.contains(t.getRawClass()));
        }
    }
}
