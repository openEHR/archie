package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nedap.archie.odin.CodePhraseSerializer;
import com.nedap.archie.odin.OdinParsingClusterMixin;
import com.nedap.archie.odin.OdinParsingItemTreeMixin;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rminfo.RMObjectMapperProvider;
import org.openehr.odin.jackson.ODINMapper;

import java.io.IOException;

public class ArchieRMObjectMapperProvider implements RMObjectMapperProvider {

    @Override
    public ObjectMapper getInputOdinObjectMapper() {
        ObjectMapper odinMapper = new ObjectMapper();
        JacksonUtil.configureObjectMapper(odinMapper);
        odinMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
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

        //ignore the @type field when not needed
        odinMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                if (propertyName.equalsIgnoreCase("@type")) {
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
        RMJacksonConfiguration config = RMJacksonConfiguration.createStandardsCompliant();
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
        RMJacksonConfiguration config = RMJacksonConfiguration.createStandardsCompliant();
        config.setAlwaysIncludeTypeProperty(false);
        config.setSerializeEmptyCollections(false);
        return JacksonUtil.getObjectMapper(config);
    }
}
