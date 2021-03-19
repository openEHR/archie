package com.nedap.archie.rm.datavalues.quantity;


import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvTemporal;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_QUANTIFIED", propOrder = {
        "magnitudeStatus"
})
@XmlSeeAlso({
        DvTemporal.class,
        DvAmount.class
})
public abstract class DvQuantified<DataValueType extends DvQuantified<DataValueType, AccuracyType, MagnitudeType>, AccuracyType, MagnitudeType extends Comparable<MagnitudeType>> extends DvOrdered<DataValueType> {

    @Nullable
    @XmlElement(name = "magnitude_status")
    private String magnitudeStatus;

    public DvQuantified() {
    }

    public DvQuantified(@Nullable List<ReferenceRange<DataValueType>> otherReferenceRanges, @Nullable DvInterval<DataValueType> normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus) {
        super(otherReferenceRanges, normalRange, normalStatus);
        this.magnitudeStatus = magnitudeStatus;
    }

    @Nullable
    public String getMagnitudeStatus() {
        return magnitudeStatus;
    }

    public void setMagnitudeStatus(@Nullable String magnitudeStatus) {
        this.magnitudeStatus = magnitudeStatus;
    }

    @Nullable
    public abstract AccuracyType getAccuracy();

    public abstract MagnitudeType getMagnitude();

    @Override
    public int compareTo(DataValueType other) {
        return getMagnitude().compareTo(other.getMagnitude());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DvQuantified<?, ?, ?> that = (DvQuantified<?, ?, ?>) o;
        return Objects.equals(magnitudeStatus, that.magnitudeStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), magnitudeStatus);
    }
}
