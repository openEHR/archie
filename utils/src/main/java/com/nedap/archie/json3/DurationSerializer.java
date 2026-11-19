package com.nedap.archie.json3;

import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.temporal.TemporalAmount;

public class DurationSerializer extends ValueSerializer<TemporalAmount> {
    @Override
    public void serialize(TemporalAmount value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(DateTimeSerializerFormatters.serializeDuration(value));
    }
}
