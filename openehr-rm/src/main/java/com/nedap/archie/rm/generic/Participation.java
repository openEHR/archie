package com.nedap.archie.rm.generic;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PARTICIPATION", propOrder = {
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
        setFunction(function);
        setMode(mode);
        setTime(time);
        setPerformer(performer);
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
        return Objects.equals(function, that.function) &&
                Objects.equals(mode, that.mode) &&
                Objects.equals(time, that.time) &&
                Objects.equals(performer, that.performer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, mode, time, performer);
    }

    @Invariant("Function_valid")
    public boolean functionValid() {
        if(function instanceof DvCodedText) {
            return InvariantUtil.belongsToTerminologyByGroupId((DvCodedText) function, "participation function");
        }
        return true;
    }

    @Invariant("Mode_valid")
    public boolean modeValid() {
        return InvariantUtil.belongsToTerminologyByGroupId(mode, "participation mode");
    }
}
