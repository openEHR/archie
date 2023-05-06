package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nedap.archie.rules.OperatorKind;

import java.io.IOException;

public class OldOperatorKindSerializer extends JsonSerializer<OperatorKind> {
    @Override
    public void serialize(OperatorKind value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.name());
        }
    }
}
