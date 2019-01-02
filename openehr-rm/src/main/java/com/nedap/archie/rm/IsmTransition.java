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
}
