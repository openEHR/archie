package com.nedap.archie.aom.primitives;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.base.Interval;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
