package com.nedap.archie.serializer.adl.jackson3;

import com.nedap.archie.aom.terminology.ArchetypeTerm;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.Map;

/**
 * ArchetypeTerm implements Map, so gets serialized with the Mapserializer instead of object. Do this manually
 */
public class ArchetypeTermOdinSerializer extends ValueSerializer<ArchetypeTerm> {

    @Override
    public void serialize(ArchetypeTerm value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeStartObject();
        gen.writeName("text");
        gen.writeString(value.getText());
        if (value.getDescription() != null) {
            gen.writeName("description");
            gen.writeString(value.getDescription());
        }
        for (Map.Entry<String, String> entry : value.getOtherItems().entrySet()) {
            gen.writeName(entry.getKey());
            gen.writeString(entry.getValue());
        }
        gen.writeEndObject();
    }
}
