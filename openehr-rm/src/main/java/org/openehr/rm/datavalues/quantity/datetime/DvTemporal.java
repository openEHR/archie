package org.openehr.rm.datavalues.quantity.datetime;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.quantity.DvAbsoluteQuantity;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.ReferenceRange;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 01/03/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_TEMPORAL")
public abstract class DvTemporal<DataValueType extends DvTemporal<DataValueType, MagnitudeType>, MagnitudeType extends Comparable<MagnitudeType>> extends DvAbsoluteQuantity<DataValueType, DvDuration, MagnitudeType> {

    @Nullable
    private DvDuration accuracy;

    public DvTemporal() {
    }

    public DvTemporal(@Nullable DvDuration accuracy) {
        this.accuracy = accuracy;
    }

    public DvTemporal(@Nullable List<ReferenceRange<DataValueType>> otherReferenceRanges, @Nullable DvInterval<DataValueType> normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus, @Nullable DvDuration accuracy) {
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

        DvTemporal<?, ?> that = (DvTemporal<?, ?>) o;

        return Objects.equals(accuracy, that.accuracy);

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accuracy);
    }
}
