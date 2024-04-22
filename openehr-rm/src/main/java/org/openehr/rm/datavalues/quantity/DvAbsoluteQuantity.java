package org.openehr.rm.datavalues.quantity;

import org.openehr.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlType(name = "DV_ABSOLUTE_QUANTITY")
public abstract class DvAbsoluteQuantity<DataValueType extends DvAbsoluteQuantity<DataValueType, AccuracyType, MagnitudeType>, AccuracyType extends DvAmount<?, ?>, MagnitudeType extends Comparable<MagnitudeType>> extends DvQuantified<DataValueType, AccuracyType, MagnitudeType> {

    public DvAbsoluteQuantity() {
    }

    protected DvAbsoluteQuantity(@Nullable List<ReferenceRange<DataValueType>> otherReferenceRanges, @Nullable DvInterval<DataValueType> normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus) {
        super(otherReferenceRanges, normalRange, normalStatus, magnitudeStatus);
    }
}
