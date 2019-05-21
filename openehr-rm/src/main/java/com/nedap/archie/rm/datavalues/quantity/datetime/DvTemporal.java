package com.nedap.archie.rm.datavalues.quantity.datetime;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.quantity.DvAbsoluteQuantity;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.ReferenceRange;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 01/03/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_TEMPORAL")
public abstract class DvTemporal<MagnitudeType extends Comparable> extends DvAbsoluteQuantity<DvDuration, MagnitudeType> {

    @Nullable
    private DvDuration accuracy;

    public DvTemporal() {
    }

    public DvTemporal(@Nullable DvDuration accuracy) {
        this.accuracy = accuracy;
    }

    public DvTemporal(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus, @Nullable DvDuration accuracy) {
        super(otherReferenceRanges, normalRange, normalStatus, magnitudeStatus);
        this.accuracy = accuracy;
    }

    @Override
    public DvDuration getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(DvDuration accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DvTemporal<?> that = (DvTemporal<?>) o;

        return accuracy != null ? accuracy.equals(that.accuracy) : that.accuracy == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (accuracy != null ? accuracy.hashCode() : 0);
        return result;
    }
}
