package org.s2.serialisation.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CTemporal;
import com.nedap.archie.json.*;
import com.nedap.archie.rules.Operator;
import com.nedap.archie.rules.OperatorKind;
import org.s2.rm.base.model_support.archetyped.Pathable;
import org.s2.rm.base.model_support.identification.ArchetypeId;
import org.s2.rminfo.S2RmInfoLookup;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to obtain an ObjectMapper that works with both archie RM and AOM objects, serializing into
 * a JSON with openEHR-spec type names.
 *
 * When a standard is agreed upon in the openEHR-specs, this format will likely change.
 *
 * Created by pieter.bos on 30/06/16.
 */
public class S2RmJacksonUtil {

    //threadsafe, can be cached
    private static final ConcurrentHashMap<ArchieJacksonConfiguration, ObjectMapper> objectMapperByConfiguration = new ConcurrentHashMap<>();

    /**
     * Get an object mapper that works with Archie RM and AOM objects. It will be cached in a static variable for
     * performance reasons. returns a standards compliant version of the object mapper.
     * @return the requested object mapper.
     */
    public static ObjectMapper getObjectMapper() {
        return getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
    }

    public static ObjectMapper getObjectMapper(ArchieJacksonConfiguration configuration) {
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
     * Creates a standards compliant version of the object mapper
     * @param objectMapper the object mapper to configure
     */
    public static void configureObjectMapper(ObjectMapper objectMapper) {
        configureObjectMapper(objectMapper, ArchieJacksonConfiguration.createStandardsCompliant());
    }

    public static void configureObjectMapper(ObjectMapper objectMapper, ArchieJacksonConfiguration configuration) {
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
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        if(!configuration.isSerializeEmptyCollections()) {
            objectMapper.setDefaultPropertyInclusion(
                    JsonInclude.Value.construct(
                            JsonInclude.Include.CUSTOM,
                            JsonInclude.Include.USE_DEFAULTS,
                            ExcludeEmptyCollectionsFilter.class,
                            null));
        }
        if(configuration.isFailOnUnknownProperties()) {
            objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        } else {
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new ArchieDurationModule());


        SimpleModule module = new SimpleModule("archie-module");
        if(!configuration.isAddExtraFieldsInArchetypeId()) {
            module.setMixInAnnotation(ArchetypeId.class, FixArchetypeIDMixin.class);
        }

        if(!configuration.isAddPathProperty()) {
            module.setMixInAnnotation(Pathable.class, DontSerializePathMixin.class);
        }
        if(configuration.isArchetypeBooleanIsPrefix()) {
            module.setMixInAnnotation(Archetype.class, IsPrefixArchetypeMixin.class);
            module.setMixInAnnotation(ArchetypeSlot.class, IsPrefixArchetypeSlotMixin.class);
            module.setMixInAnnotation(AuthoredResource.class, IsPrefixAuthoredResourceMixin.class);
            module.setMixInAnnotation(CObject.class, IsPrefixCObjectMixin.class);
            module.setMixInAnnotation(CPrimitiveObject.class, IsPrefixCPrimitiveObjectMixin.class);
        }

        if(configuration.isAddPatternConstraintTypo()) {
            module.setMixInAnnotation(CTemporal.class, PatternConstraintCTemporalMixin.class);
        }
        if(configuration.isStandardsCompliantExpressions()) {
            module.setMixInAnnotation(RulesSection.class, RulesSectionMixin.class);
        } else {
            module.addSerializer(OperatorKind.class, new OldOperatorKindSerializer());
            module.setMixInAnnotation(Operator.class, OperatorLegacyFormatMixin.class);
        }
        //make rules parsing work both for a list and a RulesSection object
        module.addDeserializer(RulesSection.class, new RulesSectionDeserializer());

        objectMapper.registerModule(module);

        objectMapper.enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL);

        TypeResolverBuilder<?> typeResolverBuilder = new ArchieTypeResolverBuilder(S2RmInfoLookup.getInstance(), configuration)
                .init(JsonTypeInfo.Id.NAME, new ArchieTypeNameResolver(S2RmInfoLookup.getInstance(), configuration.isStandardsCompliantExpressions()))
                .typeProperty(configuration.getTypePropertyName())
                .typeIdVisibility(true)
                .inclusion(JsonTypeInfo.As.PROPERTY);

        //_type is always allowed as an extra property, even if we don't expect it.
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

}
