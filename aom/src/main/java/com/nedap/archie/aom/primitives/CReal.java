package com.nedap.archie.aom.primitives;

import com.nedap.archie.base.Interval;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

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
    @Nullable
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
}
