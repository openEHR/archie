package com.nedap.archie.xml.adapters;

import java.time.temporal.TemporalAccessor;

/**
 * Created by pieter.bos on 28/07/16.
 */
public class DateIntervalXmlAdapter extends AbstractIntervalAdapter<String, TemporalAccessor> {

    public DateIntervalXmlAdapter() {
        super(new DateTimeXmlAdapter());
    }

}
