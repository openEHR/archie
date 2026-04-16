package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nedap.archie.datetime.DateTimeParsers;

import java.io.IOException;
import java.time.temporal.TemporalAccessor;

/**
 * Created by pieter.bos on 30/06/16.
 */
public class DateTimeDeserializer extends JsonDeserializer<TemporalAccessor>{
    @Override
    public TemporalAccessor deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String valueAsString = p.getValueAsString();
        if(valueAsString == null || valueAsString.isEmpty()) {
            return null;
        }
        return DateTimeParsers.parseDateTimeValue(valueAsString);
    }

}
