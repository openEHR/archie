package com.nedap.archie.rm.generic;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PARTICIPATION", propOrder = {
        "function",
        "mode",
        "time",
        "performer"
})
public class Participation extends RMObject {

    private DvText function;
    @Nullable
    private DvCodedText mode;
    @Nullable
    private DvInterval<DvDateTime> time;
    private PartyProxy performer;

    public Participation() {
    }

    public Participation(PartyProxy performer, DvText function, @Nullable DvCodedText mode, @Nullable DvInterval<DvDateTime> time) {
        this.function = function;
        this.mode = mode;
        this.time = time;
        this.performer = performer;
    }

    public DvText getFunction() {
        return function;
    }

    public void setFunction(DvText function) {
        this.function = function;
    }

    @Nullable
    public DvCodedText getMode() {
        return mode;
    }

    public void setMode(@Nullable DvCodedText mode) {
        this.mode = mode;
    }

    @Nullable
    public DvInterval<DvDateTime> getTime() {
        return time;
    }

    public void setTime(@Nullable DvInterval<DvDateTime> time) {
        this.time = time;
    }

    public PartyProxy getPerformer() {
        return performer;
    }

    public void setPerformer(PartyProxy performer) {
        this.performer = performer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participation that = (Participation) o;

        if (function != null ? !function.equals(that.function) : that.function != null) return false;
        if (mode != null ? !mode.equals(that.mode) : that.mode != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        return performer != null ? performer.equals(that.performer) : that.performer == null;

    }

    @Override
    public int hashCode() {
        int result = function != null ? function.hashCode() : 0;
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (performer != null ? performer.hashCode() : 0);
        return result;
    }
}
