package com.nedap.archie.rm.datavalues.quantity;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.DvText;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "REFERENCE_RANGE", propOrder = {
        "meaning",
        "range"
})
public class ReferenceRange<T extends DvOrdered> extends RMObject {

    private DvInterval<T> range;
    private DvText meaning;

    public ReferenceRange() {
    }

    public ReferenceRange(DvText meaning, DvInterval<T> range) {
        this.range = range;
        this.meaning = meaning;
    }

    public DvInterval<T> getRange() {
        return range;
    }

    public void setRange(DvInterval<T> range) {
        this.range = range;
    }

    public DvText getMeaning() {
        return meaning;
    }

    public void setMeaning(DvText meaning) {
        this.meaning = meaning;
    }



}
