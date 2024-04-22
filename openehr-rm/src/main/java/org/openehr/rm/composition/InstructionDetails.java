package org.openehr.rm.composition;

import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.support.identification.LocatableRef;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

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

        InstructionDetails that = (InstructionDetails) o;

        return Objects.equals(instructionId, that.instructionId) &&
                Objects.equals(activityId, that.activityId) &&
                Objects.equals(wfDetails, that.wfDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructionId, activityId, wfDetails);
    }

    @Invariant("Activity_path_valid")
    public boolean actionArchetypeIdValid() {
        return InvariantUtil.nullOrNotEmpty(activityId);
    }
}
