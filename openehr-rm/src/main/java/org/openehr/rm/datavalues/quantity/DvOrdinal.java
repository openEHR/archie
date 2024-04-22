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
@XmlType(name = "DV_ORDINAL", propOrder = {
        "value",
        "symbol"
})
public class DvOrdinal extends DvOrdered<DvOrdinal> implements SingleValuedDataValue<Long>, Comparable<DvOrdinal> {

    private DvCodedText symbol;
    private Long value;

    public DvOrdinal() {
    }

    public DvOrdinal(Long value, DvCodedText symbol) {
        this.symbol = symbol;
        this.value = value;
    }

    public DvOrdinal(@Nullable List<ReferenceRange<DvOrdinal>> otherReferenceRanges, @Nullable DvInterval<DvOrdinal> normalRange, Long value, DvCodedText symbol) {
        super(otherReferenceRanges, normalRange);
        this.symbol = symbol;
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public int compareTo(DvOrdinal o) {
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
        DvOrdinal dvOrdinal = (DvOrdinal) o;
        return Objects.equals(symbol, dvOrdinal.symbol) &&
                Objects.equals(value, dvOrdinal.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol, value);
    }
}
