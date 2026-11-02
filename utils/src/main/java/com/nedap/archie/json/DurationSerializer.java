package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;

import java.io.IOException;
import java.time.temporal.TemporalAmount;

public class DurationSerializer extends JsonSerializer<TemporalAmount> {
    @Override
    public void serialize(TemporalAmount value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(DateTimeSerializerFormatters.serializeDuration(value));
    }
}
