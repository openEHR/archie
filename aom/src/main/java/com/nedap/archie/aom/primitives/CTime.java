package com.nedap.archie.aom.primitives;

import com.nedap.archie.base.Interval;
import com.nedap.archie.xml.adapters.TimeIntervalXmlAdapter;
import com.nedap.archie.xml.adapters.TimeXmlAdapter;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_TIME")
@XmlAccessorType(XmlAccessType.FIELD)
public class CTime extends CTemporal<TemporalAccessor> {

    @XmlJavaTypeAdapter(TimeXmlAdapter.class)
    @XmlElement(name="assumed_value")
    @Nullable
    private TemporalAccessor assumedValue;
    @XmlJavaTypeAdapter(TimeIntervalXmlAdapter.class)
    private List<Interval<TemporalAccessor>> constraint = new ArrayList<>();

    @Override
    public TemporalAccessor getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(TemporalAccessor assumedValue) {
        this.assumedValue = assumedValue;
    }

    @Override
    public List<Interval<TemporalAccessor>> getConstraint() {
        return constraint;
    }

    @Override
    public void setConstraint(List<Interval<TemporalAccessor>> constraint) {
        this.constraint = constraint;
    }

    @Override
    public void addConstraint(Interval<TemporalAccessor> constraint) {
        this.constraint.add(constraint);
    }

}
