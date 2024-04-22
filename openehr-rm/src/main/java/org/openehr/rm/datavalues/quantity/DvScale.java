package org.openehr.rm.datavalues.quantity;

import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.SingleValuedDataValue;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_SCALE", propOrder = {
        "value",
        "symbol"
})
public class DvScale extends DvOrdered<DvScale> implements SingleValuedDataValue<Double>, Comparable<DvScale> {

    private DvCodedText symbol;
    private Double value;

    public DvScale() {
    }

    public DvScale(Double value, DvCodedText symbol) {
        this.symbol = symbol;
        this.value = value;
    }

    public DvScale(@Nullable List<ReferenceRange<DvScale>> otherReferenceRanges, @Nullable DvInterval<DvScale> normalRange, Double value, DvCodedText symbol) {
        super(otherReferenceRanges, normalRange);
        this.symbol = symbol;
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public int compareTo(DvScale o) {
        return value.compareTo(o.value);
    }


    public DvCodedText getSymbol() {
        return symbol;
    }

    public void setSymbol(DvCodedText symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DvScale dvScale = (DvScale) o;
        return Objects.equals(symbol, dvScale.symbol) &&
                Objects.equals(value, dvScale.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol, value);
    }
}
