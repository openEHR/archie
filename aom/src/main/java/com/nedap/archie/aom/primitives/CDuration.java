package com.nedap.archie.aom.primitives;

import com.nedap.archie.base.Interval;
import com.nedap.archie.xml.adapters.DurationIntervalXmlAdapter;
import com.nedap.archie.xml.adapters.DurationXmlAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CDuration)) return false;
        if (!super.equals(o)) return false;
        CDuration cDuration = (CDuration) o;
        return Objects.equals(assumedValue, cDuration.assumedValue) && Objects.equals(constraint, cDuration.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), assumedValue, constraint);
    }
}
