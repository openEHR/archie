package org.openehr.bmm.v2.persistence.jackson3;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;
import com.nedap.archie.base.OpenEHRBase;

/**
 * Jackson 3 port of {@link org.openehr.bmm.v2.persistence.jackson.BmmJacksonUtil}.
 */
public class BmmJacksonUtil3 {

    private volatile static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = createObjectMapper();
        }
        return objectMapper;
    }

    public static ObjectMapper createObjectMapper() {
        BmmTypeResolverBuilder3 typeResolverBuilder = new BmmTypeResolverBuilder3();
        typeResolverBuilder.typeIdVisibility(true);
        typeResolverBuilder.init(
                JsonTypeInfo.Value.construct(JsonTypeInfo.Id.NAME, JsonTypeInfo.As.PROPERTY,
                        "_type", null, true, null),
                new BmmTypeNaming3());

        return JsonMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public boolean handleUnknownProperty(DeserializationContext ctxt, tools.jackson.core.JsonParser p,
                                                         ValueDeserializer<?> deserializer, Object beanOrClass,
                                                         String propertyName) {
                        if (propertyName.equalsIgnoreCase("_type")) {
                            return true;
                        }
                        return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
                    }
                })
                .setDefaultTyping(typeResolverBuilder)
                .build();
    }

    static class BmmTypeResolverBuilder3 extends DefaultTypeResolverBuilder {
        public BmmTypeResolverBuilder3() {
            super(BasicPolymorphicTypeValidator.builder().allowIfBaseType(OpenEHRBase.class).build(),
                    DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }

        @Override
        public boolean useForType(JavaType t) {
            return OpenEHRBase.class.isAssignableFrom(t.getRawClass()) && super.useForType(t);
        }
    }
}
