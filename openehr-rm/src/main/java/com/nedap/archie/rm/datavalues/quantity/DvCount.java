package com.nedap.archie.rm.datavalues.quantity;

import com.nedap.archie.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_COUNT")
public class DvCount extends DvAmount<Long> {

    private Long magnitude;

    public DvCount() {
    }

    public DvCount(Long magnitude) {
        this.magnitude = magnitude;
    }

    public DvCount(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, @Nullable CodePhrase normalStatus, @Nullable Double accuracy, @Nullable Boolean accuracyIsPercent, @Nullable String magnitudeStatus, Long magnitude) {
        super(otherReferenceRanges, normalRange, normalStatus, accuracy, accuracyIsPercent, magnitudeStatus);
        this.magnitude = magnitude;
    }

    @Override
    public Long getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Long magnitude) {
        this.magnitude = magnitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DvCount dvCount = (DvCount) o;

        return magnitude != null ? magnitude.equals(dvCount.magnitude) : dvCount.magnitude == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (magnitude != null ? magnitude.hashCode() : 0);
        return result;
    }
}
