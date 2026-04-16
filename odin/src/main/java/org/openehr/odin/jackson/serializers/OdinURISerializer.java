package org.openehr.odin.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.net.URI;

public class OdinURISerializer extends JsonSerializer<URI> {
    @Override
    public void serialize(URI value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeRawValue(value.toString());
    }
}
