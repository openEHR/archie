package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredResource;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.json.RMJacksonConfiguration;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rm.datavalues.DvCodedText;

import java.io.IOException;

public class DefaultValueDeserializer extends JsonDeserializer<OpenEHRBase> {

    public DefaultValueDeserializer() {
        System.out.println("me!");
    }

    private static final ObjectMapper objectMapper = getObjectMapper(new RMJacksonConfiguration());

    private static final ObjectMapper getObjectMapper(RMJacksonConfiguration config) {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonUtil.configureObjectMapper(objectMapper, config);

        //objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

//        objectMapper.addMixIn(TerminologyCode.class, TerminologyIdParsingTerminologyCodeMixin.class);
//        objectMapper.addMixIn(AuthoredResource.class, AuthoredResourceMixin.class);
//        objectMapper.addMixIn(Archetype.class, ArchetypeMixin.class);
//        objectMapper.addMixIn(Template.class, ArchetypeMixin.class);
//        objectMapper.addMixIn(CArchetypeRoot.class, CArchetypeRootMixin.class);
//        objectMapper.addMixIn(CComplexObject.class, CComplexObjectMixin.class);
//
//        objectMapper.addMixIn(CTerminologyCode.class, CTerminologyCodeMixin.class);
        return objectMapper;
    }


    @Override
    public OpenEHRBase deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return objectMapper.readValue(p, OpenEHRBase.class);
    }
}
