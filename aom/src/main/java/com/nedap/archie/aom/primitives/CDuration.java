package com.nedap.archie.aom.primitives;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nedap.archie.base.Interval;
import com.nedap.archie.json.DurationDeserializer;
import com.nedap.archie.json.DurationIntervalToStringMapper;
import com.nedap.archie.json.DurationSerializer;
import com.nedap.archie.json.StringIntervalToDurationMapper;
import com.nedap.archie.xml.adapters.DurationIntervalXmlAdapter;
import com.nedap.archie.xml.adapters.DurationXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_DURATION")
@XmlAccessorType(XmlAccessType.FIELD)
public class CDuration extends CTemporal<TemporalAmount> {

    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @XmlElement(name="assumed_value")
    private TemporalAmount assumedValue;
    @XmlJavaTypeAdapter(DurationIntervalXmlAdapter.class)
    private List<Interval<TemporalAmount>> constraint = new ArrayList<>();

    @Override
    public TemporalAmount getAssumedValue() {
        return assumedValue;
    }

    @Override
    @JsonDeserialize(using= DurationDeserializer.class)
    @JsonSerialize(using= DurationSerializer.class)
    public void setAssumedValue(TemporalAmount assumedValue) {
        this.assumedValue = assumedValue;
    }
    
    @Override
    public List<Interval<TemporalAmount>> getConstraint() {
        return constraint;
    }

    @Override
    //@JsonDeserialize(converter= StringIntervalToDurationMapper.class)
    public void setConstraint(List<Interval<TemporalAmount>> constraint) {
        this.constraint = constraint;
    }


    @Override
    public void addConstraint(Interval<TemporalAmount> constraint) {
        this.constraint.add(constraint);
    }

}
