package org.openehr.odin.jackson3.serializers;

import com.nedap.archie.base.Interval;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;

/**
 * Serializer that serializes Intervals directly to ODIN
 */
public class OdinIntervalSerializer extends ValueSerializer<Interval> {

    @Override
    public void serialize(Interval value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeRaw(value.toString());
    }

    @Override
    public void serializeWithType(Interval value, JsonGenerator gen, SerializationContext ctxt, TypeSerializer typeSer) throws JacksonException {
        gen.writeRawValue(value.toString());
    }
}
