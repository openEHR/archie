package org.openehr.odin.jackson3.serializers;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.net.URL;

public class OdinURLSerializer extends ValueSerializer<URL> {

    @Override
    public void serialize(URL value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeRawValue(value.toString());
    }
}
