package com.nedap.archie.adl14.aom14;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.terminology.TerminologyCode;

import java.util.Arrays;

public class CDVOrdinalItem {

    private Integer value;
    private TerminologyCode symbol;

    public Integer getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public TerminologyCode getSymbol() {
        return symbol;
    }

    public void setSymbol(TerminologyCode symbol) {
        this.symbol = symbol;
    }

    @JsonIgnore
    public CTerminologyCode getSymbolAdl2() {
        if(symbol == null) {
            return null;
        }
        CTerminologyCode result = new CTerminologyCode();
        result.setConstraint(Arrays.asList(symbol.toString()));
        return result;
    }

    @JsonIgnore
    public CInteger getValueAdl2() {
        if(value == null) {
            return null;
        }
        CInteger result = new CInteger();
        result.addConstraint(new Interval<>(Long.valueOf(value), Long.valueOf(value)));
        return result;
    }
}
