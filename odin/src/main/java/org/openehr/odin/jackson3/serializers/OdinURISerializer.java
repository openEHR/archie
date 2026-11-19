package org.openehr.odin.jackson3.serializers;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.net.URI;

public class OdinURISerializer extends ValueSerializer<URI> {

    @Override
    public void serialize(URI value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeRawValue(value.toString());
    }
}
