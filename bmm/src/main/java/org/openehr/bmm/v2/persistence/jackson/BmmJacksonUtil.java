package org.openehr.bmm.v2.persistence.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.nedap.archie.base.OpenEHRBase;

import java.io.IOException;

/**
 * Class to obtain an ObjectMapper that works with both archie RM and AOM objects, serializing into
 * a JSON with openEHR-spec type names.
 *
 * When a standard is agreed upon in the openEHR-specs, this format will likely change.
 *
 * Created by pieter.bos on 30/06/16.
 */
public class BmmJacksonUtil {

    //threadsafe, can be cached
    private volatile static ObjectMapper objectMapper;

    /**
     * Get an object mapper that works with Archie RM and AOM objects. It will be cached in a static variable for
     * performance reasons
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        if(objectMapper == null) {
            ObjectMapper newObjectMapper = new ObjectMapper();
            configureObjectMapper(newObjectMapper);
            // Assign to the static variable after configuration, so it can't be accessed by other thread before it is
            // fully configured.
            objectMapper = newObjectMapper;
        }
        return objectMapper;
    }

    /**
     * Configure an existing object mapper to work with Archie RM and AOM Objects.
     * Indentation is enabled. Feel free to disable again in your own code.
     * @param objectMapper
     */
    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //keywords = <"value"> is indistinguishable from keywords = <"value1", "value2">
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
        objectMapper.enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        //objectMapper.
        objectMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                if (propertyName.equalsIgnoreCase("_type")) {
                    return true;
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });

        TypeResolverBuilder<?> typeResolverBuilder = new BmmTypeResolverBuilder()
                .init(JsonTypeInfo.Id.NAME, new BmmTypeNaming())
                .typeProperty("_type")
                .typeIdVisibility(true)
                .inclusion(JsonTypeInfo.As.PROPERTY);

        objectMapper.setDefaultTyping(typeResolverBuilder);
    }

    /**
     * TypeResolverBuilder that outputs type information for all RMObject classes, but not for java classes.
     * Otherwise, you get this for an arrayList: "ARRAY_LIST: []", while you would expect "[]" without type
     */
    static class BmmTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {
        public BmmTypeResolverBuilder() {
            super(ObjectMapper.DefaultTyping.NON_FINAL, BasicPolymorphicTypeValidator.builder().allowIfBaseType(OpenEHRBase.class).build());
        }

        @Override
        public boolean useForType(JavaType t)
        {
            return OpenEHRBase.class.isAssignableFrom(t.getRawClass()) && super.useForType(t);
        }
    }

}
