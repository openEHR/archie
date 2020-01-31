package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nedap.archie.datetime.DateTimeParsers;

import java.io.IOException;
import org.threeten.bp.temporal.Temporal;


/**
 * Created by pieter.bos on 30/06/16.
 */
public class DateDeserializer extends JsonDeserializer<Temporal> {
    @Override
    public Temporal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String valueAsString = p.getValueAsString();
        if(valueAsString == null) {
            return null;
        }
        return DateTimeParsers.parseDateValue(valueAsString);
    }
}
