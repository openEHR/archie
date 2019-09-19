package com.nedap.archie.rm.datavalues.quantity;

import com.nedap.archie.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlType(name = "DV_ABSOLUTE_QUANTITY")
public abstract class DvAbsoluteQuantity<AccuracyType extends DvAmount, MagnitudeType extends Comparable> extends DvQuantified<AccuracyType, MagnitudeType> {

    public DvAbsoluteQuantity() {
    }

    protected DvAbsoluteQuantity(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus) {
        super(otherReferenceRanges, normalRange, normalStatus, magnitudeStatus);
    }
}
