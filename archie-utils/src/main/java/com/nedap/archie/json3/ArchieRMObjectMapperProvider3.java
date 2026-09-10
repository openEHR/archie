package com.nedap.archie.json3;

import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.odin.OdinParsingClusterMixin;
import com.nedap.archie.odin.OdinParsingItemTreeMixin;
import com.nedap.archie.odin3.CodePhraseSerializer;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rminfo.RMObjectMapperProvider3;
import com.nedap.archie.serializer.odin.AdlOdinToJsonConverter;
import org.openehr.odin.jackson3.ODINMapper3;
import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

/** Jackson 3 equivalent of ArchieRMObjectMapperProvider. */
public class ArchieRMObjectMapperProvider3 implements RMObjectMapperProvider3 {

    @Override
    public ObjectMapper getInputOdinObjectMapper() {
        JsonMapper.Builder builder = JsonMapper.builder();
        JacksonUtil3.configureBuilder(builder);
        builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        //keywords = <"value"> is indistinguishable from keywords = <"value1", "value2">
        builder.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        //odin sometimes does <> where it can mean either an empty array OR a null object. Nastyness
        builder.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        builder.disable(StreamReadFeature.STRICT_DUPLICATE_DETECTION);


        SimpleModule odinRmSupport = new SimpleModule();
        odinRmSupport.setMixInAnnotation(Cluster.class, OdinParsingClusterMixin.class);
        odinRmSupport.setMixInAnnotation(ItemTree.class, OdinParsingItemTreeMixin.class);
        builder.addModule(odinRmSupport);

        //ignore the _type field when not needed
        builder.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, ValueDeserializer<?> deserializer, Object beanOrClass, String propertyName) {
                if (propertyName.equalsIgnoreCase(AdlOdinToJsonConverter.TYPE_PROPERTY_NAME)) {
                    return true;
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });
        return builder.build();
    }

    @Override
    public ObjectMapper getOutputOdinObjectMapper() {
        ODINMapper3.Builder builder = ODINMapper3.builder();
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
        config.setAlwaysIncludeTypeProperty(false);
        config.setSerializeEmptyCollections(false);
        JacksonUtil3.configureBuilder(builder, config);

        SimpleModule odinRmSupport = new SimpleModule();

        odinRmSupport.addSerializer(CodePhrase.class, new CodePhraseSerializer());
        builder.addModule(odinRmSupport);

        return builder.build();
    }

    @Override
    public ObjectMapper getJsonObjectMapper() {
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
        config.setAlwaysIncludeTypeProperty(false);
        config.setSerializeEmptyCollections(false);
        return JacksonUtil3.getObjectMapper(config);
    }
}
