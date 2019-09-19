package com.nedap.archie.rm.datastructures;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDuration;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "INTERVAL_EVENT", propOrder = {
        "width",
        "sampleCount",
        "mathFunction"
})
public class IntervalEvent<Type extends ItemStructure> extends Event<Type> {

    private DvDuration width;
    @Nullable
    @XmlElement(name = "sample_count")
    private Long sampleCount;

    @XmlElement(name = "math_function", required = true)
    private DvCodedText mathFunction;

    public IntervalEvent() {
    }

    public IntervalEvent(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, DvDateTime time, Type data, @Nullable Type state, DvDuration width, DvCodedText mathFunction, @Nullable Long sampleCount) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName, time, data, state);
        this.width = width;
        this.sampleCount = sampleCount;
        this.mathFunction = mathFunction;
    }

    public DvDuration getWidth() {
        return width;
    }

    public void setWidth(DvDuration width) {
        this.width = width;
    }

    @Nullable
    public Long getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(@Nullable Long sampleCount) {
        this.sampleCount = sampleCount;
    }

    public DvCodedText getMathFunction() {
        return mathFunction;
    }

    public void setMathFunction(DvCodedText mathFunction) {
        this.mathFunction = mathFunction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IntervalEvent<?> that = (IntervalEvent<?>) o;
        return Objects.equals(width, that.width) &&
                Objects.equals(sampleCount, that.sampleCount) &&
                Objects.equals(mathFunction, that.mathFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), width, sampleCount, mathFunction);
    }
}
