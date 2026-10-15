package com.nedap.archie.json3;

import com.nedap.archie.datetime.DateTimeParsers;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.temporal.TemporalAccessor;

public class DateTimeDeserializer extends ValueDeserializer<TemporalAccessor> {
    @Override
    public TemporalAccessor deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String valueAsString = p.getValueAsString();
        if (valueAsString == null || valueAsString.isEmpty()) {
            return null;
        }
        return DateTimeParsers.parseDateTimeValue(valueAsString);
    }
}
