package com.nedap.archie.json3;

import com.nedap.archie.datetime.DateTimeParsers;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.temporal.Temporal;

public class DateDeserializer extends ValueDeserializer<Temporal> {

    @Override
    public Temporal deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String valueAsString = p.getValueAsString();
        if(valueAsString == null || valueAsString.isEmpty()) {
            return null;
        }
        return DateTimeParsers.parseDateValue(valueAsString);
    }
}
