package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nedap.archie.json3.JacksonUtil3;
import com.nedap.archie.odin.CodePhraseSerializer;
import com.nedap.archie.odin.OdinParsingClusterMixin;
import com.nedap.archie.odin.OdinParsingItemTreeMixin;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rminfo.RMObjectMapperProvider;
import com.nedap.archie.serializer.odin.AdlOdinToJsonConverter;
import org.openehr.odin.jackson.ODINMapper;
import org.openehr.odin.jackson3.ODINMapper3;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

public class ArchieRMObjectMapperProvider implements RMObjectMapperProvider {

    @Override
    public ObjectMapper getInputOdinObjectMapper() {
        ObjectMapper odinMapper = new ObjectMapper();
        JacksonUtil.configureObjectMapper(odinMapper);
        odinMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        //keywords = <"value"> is indistinguishable from keywords = <"value1", "value2">
        odinMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        //odin sometimes does <> where it can mean either an empty array OR a null object. Nastyness
        odinMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
//        if(!allowDuplicates) {
//            odinMapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
//        } else {
            odinMapper.disable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
//        }
        //TODO: add Mixins for *all* list properties that contain things other than primitive objects!
        //Or switch to JSON - the much easier option for everyone involved.
        SimpleModule odinRmSupport = new SimpleModule();
        odinRmSupport.setMixInAnnotation(Cluster.class, OdinParsingClusterMixin.class);
        odinRmSupport.setMixInAnnotation(ItemTree.class, OdinParsingItemTreeMixin.class);
        odinMapper.registerModule(odinRmSupport);

        //ignore the _type field when not needed
        odinMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                if (propertyName.equalsIgnoreCase(AdlOdinToJsonConverter.TYPE_PROPERTY_NAME)) {
                    return true;
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });
        return odinMapper;
    }

    @Override
    public ObjectMapper getOutputOdinObjectMapper() {
        ODINMapper odinMapper = new ODINMapper();
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
        config.setAlwaysIncludeTypeProperty(false);
        config.setSerializeEmptyCollections(false);
        JacksonUtil.configureObjectMapper(odinMapper, config);

        SimpleModule odinRmSupport = new SimpleModule();
        //TODO: check if this covers all native odin types, together with the types already included in the default OdinMapper
        odinRmSupport.addSerializer(CodePhrase.class, new CodePhraseSerializer());
        odinMapper.registerModule(odinRmSupport);

        return odinMapper;
    }

    @Override
    public ObjectMapper getJsonObjectMapper() {
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
        config.setAlwaysIncludeTypeProperty(false);
        config.setSerializeEmptyCollections(false);
        return JacksonUtil.getObjectMapper(config);
    }

    // Jackson 3 versions of the mappers above.

    @Override
    public tools.jackson.databind.ObjectMapper getInputOdinObjectMapper3() {
        JsonMapper.Builder builder = JsonMapper.builder();
        JacksonUtil3.configureBuilder(builder);
        builder.propertyNamingStrategy(tools.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);

        //keywords = <"value"> is indistinguishable from keywords = <"value1", "value2">
        builder.enable(tools.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        //odin sometimes does <> where it can mean either an empty array OR a null object. Nastyness
        builder.enable(tools.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        builder.disable(StreamReadFeature.STRICT_DUPLICATE_DETECTION);

        tools.jackson.databind.module.SimpleModule odinRmSupport = new tools.jackson.databind.module.SimpleModule();
        odinRmSupport.setMixInAnnotation(Cluster.class, OdinParsingClusterMixin.class);
        odinRmSupport.setMixInAnnotation(ItemTree.class, OdinParsingItemTreeMixin.class);
        builder.addModule(odinRmSupport);

        //ignore the _type field when not needed
        builder.addHandler(new tools.jackson.databind.deser.DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(tools.jackson.databind.DeserializationContext ctxt, tools.jackson.core.JsonParser p,
                                                 ValueDeserializer<?> deserializer, Object beanOrClass, String propertyName) {
                if (propertyName.equalsIgnoreCase(AdlOdinToJsonConverter.TYPE_PROPERTY_NAME)) {
                    return true;
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });
        return builder.build();
    }

    @Override
    public tools.jackson.databind.ObjectMapper getOutputOdinObjectMapper3() {
        ODINMapper3.Builder builder = ODINMapper3.builder();
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
        config.setAlwaysIncludeTypeProperty(false);
        config.setSerializeEmptyCollections(false);
        JacksonUtil3.configureBuilder(builder, config);

        tools.jackson.databind.module.SimpleModule odinRmSupport = new tools.jackson.databind.module.SimpleModule();
        odinRmSupport.addSerializer(CodePhrase.class, new com.nedap.archie.odin3.CodePhraseSerializer());
        builder.addModule(odinRmSupport);

        return builder.build();
    }

    @Override
    public tools.jackson.databind.ObjectMapper getJsonObjectMapper3() {
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
        config.setAlwaysIncludeTypeProperty(false);
        config.setSerializeEmptyCollections(false);
        return JacksonUtil3.getObjectMapper(config);
    }
}
