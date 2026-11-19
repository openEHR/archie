package com.nedap.archie.json3;

import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

public class DateTimeSerializer extends ValueSerializer<TemporalAccessor> {

    @Override
    public void serialize(TemporalAccessor temporalAccessor, JsonGenerator jsonGenerator, SerializationContext ctxt) throws JacksonException {
        if (temporalAccessor == null) {
            jsonGenerator.writeString("");
            return;
        }
        if (!temporalAccessor.isSupported(ChronoField.HOUR_OF_DAY) &&
                !temporalAccessor.isSupported(ChronoField.MINUTE_OF_HOUR) &&
                !temporalAccessor.isSupported(ChronoField.SECOND_OF_MINUTE)) {
            jsonGenerator.writeString(DateTimeSerializerFormatters.ISO_8601_DATE.format(temporalAccessor));
        } else {
            jsonGenerator.writeString(DateTimeSerializerFormatters.ISO_8601_DATE_TIME.format(temporalAccessor));
        }
    }
}
