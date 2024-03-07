package com.nedap.archie.xml.adapters;

import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

/**
 * Created by pieter.bos on 24/06/16.
 */
public class DateTimeXmlAdapter extends XmlAdapter<String, TemporalAccessor> {

    @Override
    public TemporalAccessor unmarshal(String stringValue) {
        return stringValue != null? DateTimeParsers.parseDateTimeValue(stringValue):null;
    }

    @Override
    public String marshal(TemporalAccessor value) {
        if(!value.isSupported(ChronoField.HOUR_OF_DAY) &&
                !value.isSupported(ChronoField.MINUTE_OF_HOUR) &&
                !value.isSupported(ChronoField.SECOND_OF_MINUTE)) {
            return DateTimeSerializerFormatters.ISO_8601_DATE.format(value);
        }

        return DateTimeSerializerFormatters.ISO_8601_DATE_TIME.format(value);
    }



}
