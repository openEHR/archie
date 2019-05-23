package com.nedap.archie.rm;

import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ISM_TRANSITION", propOrder = {
        "currentState",
        "transition",
        "careflowStep",
        "reason"
})
public class IsmTransition extends Pathable {

    @XmlElement(name = "current_state")
    private DvCodedText currentState;
    @Nullable
    private DvCodedText transition;

    @XmlElement(name = "careflow_step")
    @Nullable
    private DvCodedText careflowStep;

    @Nullable
    private List<DvText> reason = new ArrayList();

    public IsmTransition() {
    }

    public IsmTransition(DvCodedText currentState, @Nullable DvCodedText transition, @Nullable DvCodedText careflowStep, @Nullable List<DvText> reason) {
        this.currentState = currentState;
        this.transition = transition;
        this.careflowStep = careflowStep;
        this.reason = reason;
    }

    public DvCodedText getCurrentState() {
        return currentState;
    }

    public void setCurrentState(DvCodedText currentState) {
        this.currentState = currentState;
    }

    public DvCodedText getTransition() {
        return transition;
    }

    public void setTransition(DvCodedText transition) {
        this.transition = transition;
    }
    
    public DvCodedText getCareflowStep() {
        return careflowStep;
    }

    public void setCareflowStep(DvCodedText careflowStep) {
        this.careflowStep = careflowStep;
    }

    public List<DvText> getReason() {
        return reason;
    }

    public void setReason(List<DvText> reason) {
        this.reason = reason;
    }

    public void addReason(DvText reason) {
        this.reason.add(reason);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        IsmTransition that = (IsmTransition) o;

        if (currentState != null ? !currentState.equals(that.currentState) : that.currentState != null) return false;
        if (transition != null ? !transition.equals(that.transition) : that.transition != null) return false;
        if (careflowStep != null ? !careflowStep.equals(that.careflowStep) : that.careflowStep != null) return false;
        return reason != null ? reason.equals(that.reason) : that.reason == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (currentState != null ? currentState.hashCode() : 0);
        result = 31 * result + (transition != null ? transition.hashCode() : 0);
        result = 31 * result + (careflowStep != null ? careflowStep.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }
}
