package com.nedap.archie.xml.adapters;

import com.nedap.archie.adlparser.treewalkers.TemporalConstraintParser;
import com.nedap.archie.datetime.DateTimeParsers;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.threeten.bp.temporal.TemporalAccessor;

/**
 * Created by pieter.bos on 24/06/16.
 */
public class TimeXmlAdapter extends XmlAdapter<String, TemporalAccessor> {


    @Override
    public TemporalAccessor unmarshal(String stringValue) {
        return stringValue != null? DateTimeParsers.parseTimeValue(stringValue):null;
    }

    @Override
    public String marshal(TemporalAccessor value) {
        return value.toString();
    }
}
