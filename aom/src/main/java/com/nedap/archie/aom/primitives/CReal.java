package com.nedap.archie.aom.primitives;

import com.nedap.archie.base.Interval;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO: a real is perhaps not a double.
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_REAL")
@XmlAccessorType(XmlAccessType.FIELD)
public class CReal extends COrdered<Double> {

    @XmlElement(name="assumed_value")
    @Nullable
    private Double assumedValue;
    private List<Interval<Double>> constraint = new ArrayList<>();

    @Override
    public Double getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(Double assumedValue) {
        this.assumedValue = assumedValue;
    }

    @Override
    public List<Interval<Double>> getConstraint() {
        return constraint;
    }

    @Override
    public void setConstraint(List<Interval<Double>> constraint) {
        this.constraint = constraint;
    }

    @Override
    public void addConstraint(Interval<Double> constraint) {
        this.constraint.add(constraint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CReal)) return false;
        if (!super.equals(o)) return false;
        CReal cReal = (CReal) o;
        return Objects.equals(assumedValue, cReal.assumedValue) &&
                Objects.equals(constraint, cReal.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                assumedValue,
                constraint);
    }
}
