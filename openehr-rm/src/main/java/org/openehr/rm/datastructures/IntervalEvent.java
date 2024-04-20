package org.openehr.rm.datastructures;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.datavalues.quantity.datetime.DvDuration;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

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

    @Invariant("Math_function_validity")
    public boolean mathFunctionValid() {
        return InvariantUtil.belongsToTerminologyByGroupId(mathFunction, "event math function");
    }
}
