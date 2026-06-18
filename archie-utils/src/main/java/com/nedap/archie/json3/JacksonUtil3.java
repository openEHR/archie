package com.nedap.archie.json3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.*;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.TypeResolverBuilder;
import tools.jackson.databind.module.SimpleModule;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CTemporal;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.json.DontSerializePathMixin;
import com.nedap.archie.json.ExcludeEmptyCollectionsFilter;
import com.nedap.archie.json.FixArchetypeIDMixin;
import com.nedap.archie.json.IsPrefixArchetypeMixin;
import com.nedap.archie.json.IsPrefixArchetypeSlotMixin;
import com.nedap.archie.json.IsPrefixAuthoredResourceMixin;
import com.nedap.archie.json.IsPrefixCObjectMixin;
import com.nedap.archie.json.IsPrefixCPrimitiveObjectMixin;
import com.nedap.archie.json.OperatorLegacyFormatMixin;
import com.nedap.archie.json.PatternConstraintCTemporalMixin;
import com.nedap.archie.json.RulesSectionMixin;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.support.identification.ArchetypeID;
import com.nedap.archie.rules.Operator;
import com.nedap.archie.rules.OperatorKind;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Jackson 3 port of {@link com.nedap.archie.json.JacksonUtil}.
 * <p>
 * In Jackson 3, ObjectMapper is immutable; all configuration is done via {@link MapperBuilder}.
 * Use {@link #configureBuilder(MapperBuilder, ArchieJacksonConfiguration)} to configure a builder,
 * or {@link #getObjectMapper(ArchieJacksonConfiguration)} to get a pre-built mapper.
 */
public class JacksonUtil3 {

    private static final ConcurrentHashMap<ArchieJacksonConfiguration, ObjectMapper> objectMapperByConfiguration = new ConcurrentHashMap<>();

    public static ObjectMapper getObjectMapper() {
        return getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
    }

    public static ObjectMapper getObjectMapper(ArchieJacksonConfiguration configuration) {
        ObjectMapper objectMapper = objectMapperByConfiguration.get(configuration);
        if (objectMapper == null) {
            JsonMapper.Builder builder = JsonMapper.builder();
            configureBuilder(builder, configuration);
            objectMapper = builder.build();
            objectMapperByConfiguration.put(configuration, objectMapper);
        }
        return objectMapper;
    }

    public static void configureBuilder(MapperBuilder<?, ?> builder) {
        configureBuilder(builder, ArchieJacksonConfiguration.createStandardsCompliant());
    }

    public static void configureBuilder(MapperBuilder<?, ?> builder, ArchieJacksonConfiguration configuration) {
        builder.enable(SerializationFeature.INDENT_OUTPUT);
        builder.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        builder.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        builder.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        builder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        if (!configuration.isSerializeEmptyCollections()) {
            builder.changeDefaultPropertyInclusion(v -> JsonInclude.Value.construct(
                    JsonInclude.Include.CUSTOM,
                    JsonInclude.Include.USE_DEFAULTS,
                    ExcludeEmptyCollectionsFilter.class,
                    null));
        } else {
            builder.changeDefaultPropertyInclusion(v -> v.withValueInclusion(JsonInclude.Include.NON_NULL));
        }

        if (configuration.isFailOnUnknownProperties()) {
            builder.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        } else {
            builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }

        // JSR-310 support is built into Jackson 3's jackson-databind; no separate module needed
        builder.addModule(new ArchieDurationModule());

        SimpleModule module = new SimpleModule("archie-module");
        if (!configuration.isAddExtraFieldsInArchetypeId()) {
            module.setMixInAnnotation(ArchetypeID.class, FixArchetypeIDMixin.class);
        }
        if (!configuration.isAddPathProperty()) {
            module.setMixInAnnotation(Pathable.class, DontSerializePathMixin.class);
        }
        if (configuration.isArchetypeBooleanIsPrefix()) {
            module.setMixInAnnotation(Archetype.class, IsPrefixArchetypeMixin.class);
            module.setMixInAnnotation(ArchetypeSlot.class, IsPrefixArchetypeSlotMixin.class);
            module.setMixInAnnotation(AuthoredResource.class, IsPrefixAuthoredResourceMixin.class);
            module.setMixInAnnotation(CObject.class, IsPrefixCObjectMixin.class);
            module.setMixInAnnotation(CPrimitiveObject.class, IsPrefixCPrimitiveObjectMixin.class);
        }
        if (configuration.isAddPatternConstraintTypo()) {
            module.setMixInAnnotation(CTemporal.class, PatternConstraintCTemporalMixin.class);
        }
        if (configuration.isStandardsCompliantExpressions()) {
            module.setMixInAnnotation(RulesSection.class, RulesSectionMixin.class);
        } else {
            module.addSerializer(OperatorKind.class, new OldOperatorKindSerializer());
            module.setMixInAnnotation(Operator.class, OperatorLegacyFormatMixin.class);
        }
        module.addDeserializer(RulesSection.class, new RulesSectionDeserializer());

        builder.addModule(module);
        builder.enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL);

        TypeResolverBuilder<?> typeResolverBuilder = new ArchieTypeResolverBuilder3(configuration)
                .init(JsonTypeInfo.Value.construct(JsonTypeInfo.Id.NAME, JsonTypeInfo.As.PROPERTY,
                        configuration.getTypePropertyName(), null, true, null),
                        new OpenEHRTypeNaming3(configuration.isStandardsCompliantExpressions()));

        builder.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, tools.jackson.core.JsonParser p,
                                                 ValueDeserializer<?> deserializer, Object beanOrClass,
                                                 String propertyName) {
                if (propertyName.equalsIgnoreCase(configuration.getTypePropertyName())) {
                    return true;
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });

        builder.setDefaultTyping(typeResolverBuilder);
    }
}
