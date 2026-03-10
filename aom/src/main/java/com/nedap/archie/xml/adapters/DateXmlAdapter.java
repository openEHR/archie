package com.nedap.archie.xml.adapters;

import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.Temporal;

/**
 * Created by pieter.bos on 24/06/16.
 */
public class DateXmlAdapter extends XmlAdapter<String, Temporal> {

    @Override
    public Temporal unmarshal(String stringValue) {
        return stringValue != null? DateTimeParsers.parseDateValue(stringValue):null;
    }

    @Override
    public String marshal(Temporal value) {
        if(value instanceof LocalDate || value instanceof YearMonth) {
            return value.toString();
        }
        return value != null ? DateTimeSerializerFormatters.ISO_8601_DATE.format(value):null;
    }
}
