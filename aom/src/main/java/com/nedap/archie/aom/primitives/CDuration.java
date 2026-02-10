package com.nedap.archie.aom.primitives;

import com.nedap.archie.base.Interval;
import com.nedap.archie.xml.adapters.DurationIntervalXmlAdapter;
import com.nedap.archie.xml.adapters.DurationXmlAdapter;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
    @Nullable
    private TemporalAmount assumedValue;
    @XmlJavaTypeAdapter(DurationIntervalXmlAdapter.class)
    private List<Interval<TemporalAmount>> constraint = new ArrayList<>();

    @Override
    public TemporalAmount getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(TemporalAmount assumedValue) {
        this.assumedValue = assumedValue;
    }
    
    @Override
    public List<Interval<TemporalAmount>> getConstraint() {
        return constraint;
    }

    @Override
    public void setConstraint(List<Interval<TemporalAmount>> constraint) {
        this.constraint = constraint;
    }


    @Override
    public void addConstraint(Interval<TemporalAmount> constraint) {
        this.constraint.add(constraint);
    }

}
