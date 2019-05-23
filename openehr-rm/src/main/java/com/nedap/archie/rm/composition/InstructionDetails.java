package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.support.identification.LocatableRef;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "INSTRUCTION_DETAILS", propOrder = {
        "instructionId",
        "activityId",
        "wfDetails"
})
public class InstructionDetails extends Pathable {

    @XmlElement(name = "instruction_id")
    private LocatableRef instructionId;
    @XmlElement(name = "activity_id")
    private String activityId;
    @XmlElement(name = "wf_details")
    @Nullable
    private ItemStructure wfDetails;

    public InstructionDetails() {
    }

    public InstructionDetails(LocatableRef instructionId, String activityId, @Nullable ItemStructure wfDetails) {
        this.instructionId = instructionId;
        this.activityId = activityId;
        this.wfDetails = wfDetails;
    }

    public LocatableRef getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(LocatableRef instructionId) {
        this.instructionId = instructionId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }


    public ItemStructure getWfDetails() {
        return wfDetails;
    }

    public void setWfDetails(ItemStructure wfDetails) {
        this.wfDetails = wfDetails;
        setThisAsParent(wfDetails, "wf_details");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        InstructionDetails that = (InstructionDetails) o;

        if (instructionId != null ? !instructionId.equals(that.instructionId) : that.instructionId != null)
            return false;
        if (activityId != null ? !activityId.equals(that.activityId) : that.activityId != null) return false;
        return wfDetails != null ? wfDetails.equals(that.wfDetails) : that.wfDetails == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (instructionId != null ? instructionId.hashCode() : 0);
        result = 31 * result + (activityId != null ? activityId.hashCode() : 0);
        result = 31 * result + (wfDetails != null ? wfDetails.hashCode() : 0);
        return result;
    }
}
