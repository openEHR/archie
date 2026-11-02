package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;

import java.io.IOException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

/**
 * Created by pieter.bos on 30/09/16.
 */
public class DateTimeSerializer extends JsonSerializer<TemporalAccessor> {


    @Override
    public void serialize(TemporalAccessor temporalAccessor, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(temporalAccessor == null) {
            jsonGenerator.writeString("");
        }
        if(!temporalAccessor.isSupported(ChronoField.HOUR_OF_DAY) &&
                !temporalAccessor.isSupported(ChronoField.MINUTE_OF_HOUR) &&
                !temporalAccessor.isSupported(ChronoField.SECOND_OF_MINUTE)) {
            jsonGenerator.writeString(DateTimeSerializerFormatters.ISO_8601_DATE.format(temporalAccessor));
        } else {
            jsonGenerator.writeString(DateTimeSerializerFormatters.ISO_8601_DATE_TIME.format(temporalAccessor));
        }
    }

}
