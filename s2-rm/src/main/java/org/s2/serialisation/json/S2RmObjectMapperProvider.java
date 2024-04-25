package org.s2.serialisation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.rminfo.RMObjectMapperProvider;
import com.nedap.archie.serializer.odin.AdlOdinToJsonConverter;
import org.openehr.odin.jackson.ODINMapper;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.serialisation.odin.OdinParsingNodeMixin;

import java.io.IOException;

public class S2RmObjectMapperProvider implements RMObjectMapperProvider {

    @Override
    public ObjectMapper getInputOdinObjectMapper() {
        ObjectMapper odinMapper = new ObjectMapper();
        S2RmJacksonUtil.configureObjectMapper(odinMapper);
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
        odinRmSupport.setMixInAnnotation(Node.class, OdinParsingNodeMixin.class);
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
        S2RmJacksonUtil.configureObjectMapper(odinMapper, config);

        SimpleModule odinRmSupport = new SimpleModule();
        //TODO: check if this covers all native odin types, together with the types already included in the default OdinMapper
        // odinRmSupport.addSerializer(CodePhrase.class, new CodePhraseSerializer());
        odinMapper.registerModule(odinRmSupport);

        return odinMapper;
    }

    @Override
    public ObjectMapper getJsonObjectMapper() {
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
        config.setAlwaysIncludeTypeProperty(false);
        config.setSerializeEmptyCollections(false);
        return S2RmJacksonUtil.getObjectMapper(config);
    }
}
