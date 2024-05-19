package org.openehr.rm.composition;

import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.DvText;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<DvText> reason = new ArrayList<>();

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
        IsmTransition that = (IsmTransition) o;
        return Objects.equals(currentState, that.currentState) &&
                Objects.equals(transition, that.transition) &&
                Objects.equals(careflowStep, that.careflowStep) &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentState, transition, careflowStep, reason);
    }

    @Invariant("Current_state_valid")
    public boolean currentStateValid() {
        return InvariantUtil.belongsToTerminologyByGroupId(currentState, "instruction states");
    }

    @Invariant("Transition_valid")
    public boolean transitionValid() {
        return InvariantUtil.belongsToTerminologyByGroupId(transition, "instruction transitions");
    }
}
