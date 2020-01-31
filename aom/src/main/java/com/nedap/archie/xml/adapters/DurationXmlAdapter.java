package com.nedap.archie.xml.adapters;

import com.nedap.archie.datetime.DateTimeParsers;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.threeten.bp.temporal.TemporalAmount;

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
        return value.toString();//java toString of Period and Duration is the ISO-8601 format
    }
}