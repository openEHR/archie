package com.nedap.archie.rm.datavalues.quantity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.base.Interval;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rminfo.Invariant;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "DV_INTERVAL", propOrder = {
        "lower",
        "upper",
        "lowerIncluded",
        "upperIncluded",
        "lowerUnbounded",
        "upperUnbounded"
})
public class DvInterval<Type extends DvOrdered> extends DataValue {

    private final Interval<DvOrdered> interval;

    public DvInterval() {
        interval = new Interval<>();
    }

    public DvInterval(Type lower, Type upper) {
        interval = new Interval<>(lower, upper);
    }

    @Nullable
    public DvOrdered getLower() {
        return interval.getLower();
    }

    public void setLower(DvOrdered lower) {
        interval.setLower(lower);
    }

    @Nullable
    public DvOrdered getUpper() {
        return interval.getUpper();
    }

    public void setUpper(DvOrdered upper) {
        interval.setUpper(upper);
    }

    @XmlElement(name = "lower_unbounded")
    public boolean isLowerUnbounded() {
        return interval.isLowerUnbounded();
    }

    public void setLowerUnbounded(boolean lowerUnbounded) {
        interval.setLowerUnbounded(lowerUnbounded);
    }

    @XmlElement(name = "upper_unbounded")
    public boolean isUpperUnbounded() {
        return interval.isUpperUnbounded();
    }

    public void setUpperUnbounded(boolean upperUnbounded) {
        interval.setUpperUnbounded(upperUnbounded);
    }

    @XmlElement(name = "lower_included")
    public boolean isLowerIncluded() {
        return interval.isLowerIncluded();
    }

    public void setLowerIncluded(boolean lowerIncluded) {
        interval.setLowerIncluded(lowerIncluded);
    }

    @XmlElement(name = "upper_included")
    public boolean isUpperIncluded() {
        return interval.isUpperIncluded();
    }

    public void setUpperIncluded(boolean upperIncluded) {
        interval.setUpperIncluded(upperIncluded);
    }

    /**
     * gets the underlying interval, so it can be access and its invariants are validated
     * @return
     */
    @JsonIgnore
    @XmlTransient
    public Interval<DvOrdered> getInterval() {
        return interval;
    }

    public boolean has(DvOrdered value) {
        return interval.has(value);
    }

    public String toString() {
        return interval.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DvInterval<?> that = (DvInterval<?>) o;
        return Objects.equals(interval, that.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval);
    }

    @Invariant("Limits_consistent")
    public boolean limitsConsistent() {
        if(!isLowerUnbounded() && !isUpperUnbounded()) {
            return getLower() == null || getUpper() == null ||
                    interval.getComparableLower().compareTo(interval.getComparableUpper()) <= 0;
        }
        return true;
    }

}
