package com.nedap.archie.xml.adapters;

import java.time.temporal.TemporalAmount;

/**
 * Created by pieter.bos on 28/07/16.
 */
public class DurationIntervalXmlAdapter extends AbstractIntervalAdapter<String, TemporalAmount> {

    public DurationIntervalXmlAdapter() {
        super(new DurationXmlAdapter());
    }
}
