package com.nedap.archie.json3;

import com.nedap.archie.rules.OperatorKind;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class OldOperatorKindSerializer extends ValueSerializer<OperatorKind> {

    @Override
    public void serialize(OperatorKind value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.name());
        }
    }
}
