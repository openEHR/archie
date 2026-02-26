package org.openehr.odin.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.nedap.archie.base.Interval;

import java.io.IOException;

/**
 * Serializer that serializes Intervals directly to ODIN
 */
public class OdinIntervalSerializer extends JsonSerializer<Interval> {

    @Override
    public void serialize(Interval value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeRaw(value.toString());
    }

    public void serializeWithType(Interval value, JsonGenerator gen, SerializerProvider serializers,
                                  TypeSerializer typeSer)
            throws IOException
    {
        gen.writeRawValue(value.toString());
    }
}
