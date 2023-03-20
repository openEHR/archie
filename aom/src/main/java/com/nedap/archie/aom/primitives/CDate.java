package com.nedap.archie.aom.primitives;

import com.nedap.archie.base.Interval;
import com.nedap.archie.xml.adapters.DateIntervalXmlAdapter;
import com.nedap.archie.xml.adapters.DateXmlAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="C_DATE")
public class CDate extends CTemporal<Temporal> {

    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    @XmlElement(name="assumed_value")
    @Nullable
    private Temporal assumedValue;
    @XmlJavaTypeAdapter(DateIntervalXmlAdapter.class)
    private List<Interval<Temporal>> constraint = new ArrayList<>();

    @Override
    public Temporal getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(Temporal assumedValue) {
        this.assumedValue = assumedValue;
    }

    @Override
    public List<Interval<Temporal>> getConstraint() {
        return constraint;
    }

    @Override
    public void setConstraint(List<Interval<Temporal>> constraint) {
        this.constraint = constraint;
    }

    @Override
    public void addConstraint(Interval<Temporal> constraint) {
        this.constraint.add(constraint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CDate)) return false;
        if (!super.equals(o)) return false;
        CDate cDate = (CDate) o;
        return Objects.equals(assumedValue, cDate.assumedValue) &&
                Objects.equals(constraint, cDate.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                assumedValue,
                constraint);
    }
}
