package com.nedap.archie.xml.adapters;

import java.time.temporal.TemporalAccessor;

/**
 * Created by pieter.bos on 28/07/16.
 */
public class DateTimeIntervalXmlAdapter extends AbstractIntervalAdapter<String, TemporalAccessor> {
    public DateTimeIntervalXmlAdapter() {
        super(new DateTimeXmlAdapter());
    }
}
