package com.nedap.archie.rm.datavalues.quantity;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.SingleValuedDataValue;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

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

    public DvOrdinal(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, Long value, DvCodedText symbol) {
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

        DvOrdinal dvOrdinal = (DvOrdinal) o;

        if (symbol != null ? !symbol.equals(dvOrdinal.symbol) : dvOrdinal.symbol != null) return false;
        return value != null ? value.equals(dvOrdinal.value) : dvOrdinal.value == null;

    }

    @Override
    public int hashCode() {
        int result = symbol != null ? symbol.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
