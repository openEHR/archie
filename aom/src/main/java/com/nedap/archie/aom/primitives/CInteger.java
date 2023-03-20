package com.nedap.archie.aom.primitives;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.base.Interval;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_INTEGER")
@XmlAccessorType(XmlAccessType.FIELD)
public class CInteger extends COrdered<Long> {
    @XmlElement(name="assumed_value")
    @Nullable
    private Long assumedValue;
    private List<Interval<Long>> constraint = new ArrayList<>();

    @Override
    public Long getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(Long assumedValue) {
        this.assumedValue = assumedValue;
    }

    @Override
    public List<Interval<Long>> getConstraint() {
        return constraint;
    }

    @Override
    public void setConstraint(List<Interval<Long>> constraint) {
        this.constraint = constraint;
    }

    @Override
    public void addConstraint(Interval<Long> constraint) {
        this.constraint.add(constraint);
    }

    @JsonIgnore
    @Transient
    public List<Long> getConstraintValues() {
        List<Long> result = new ArrayList<>();
        for(Interval<Long> singleConstraint:constraint) {
            if(singleConstraint.isLowerUnbounded() || singleConstraint.isUpperUnbounded()) {
                throw new RuntimeException("cannot get the constraint values of an unbounded Integer64 constraint");
            }
            long constraintLower = singleConstraint.getLower();
            if(!singleConstraint.isLowerIncluded()) {
                constraintLower++;
            }
            long constraintUpper = singleConstraint.getUpper();
            if(singleConstraint.isUpperIncluded()) {
                constraintUpper++;
            }
            for(long i = constraintLower; i < constraintUpper; i++) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CInteger)) return false;
        if (!super.equals(o)) return false;
        CInteger cInteger = (CInteger) o;
        return Objects.equals(assumedValue, cInteger.assumedValue) &&
                Objects.equals(constraint, cInteger.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                assumedValue,
                constraint);
    }
}
