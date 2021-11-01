package com.nedap.archie.serializer.adl.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nedap.archie.aom.terminology.ArchetypeTerm;

import java.io.IOException;
import java.util.Map;

/**
 * ArchetypeTerm implements Map, so gets serialized with the Mapserializer instead of object. Do this manually
 */
public class ArchetypeTermOdinSerializer extends JsonSerializer<ArchetypeTerm> {
    @Override
    public void serialize(ArchetypeTerm value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("text");
        gen.writeString(value.getText());
        if(value.getDescription() != null) {
            gen.writeFieldName("description");
            gen.writeString(value.getDescription());
        }
        for (Map.Entry<String, String> entry : value.getOtherItems().entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            gen.writeFieldName(k);
            gen.writeString(v);
        }
        gen.writeEndObject();
    }

}


