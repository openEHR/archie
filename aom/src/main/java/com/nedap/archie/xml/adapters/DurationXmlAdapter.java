package com.nedap.archie.xml.adapters;

import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.temporal.TemporalAmount;

/**
 * Created by pieter.bos on 30/06/16.
 */
public class DurationXmlAdapter extends XmlAdapter<String, TemporalAmount> {

    @Override
    public TemporalAmount unmarshal(String stringValue) {
        return stringValue != null? DateTimeParsers.parseDurationValue(stringValue):null;
    }

    @Override
    public String marshal(TemporalAmount value) {
        return DateTimeSerializerFormatters.serializeDuration(value);
    }
}