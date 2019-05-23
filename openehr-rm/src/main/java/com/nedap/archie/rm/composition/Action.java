package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.IsmTransition;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyProxy;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ACTION", propOrder = {
        "time",
        "description",
        "ismTransition",
        "instructionDetails"
})
public class Action extends CareEntry {

    private DvDateTime time;
    private ItemStructure description;
    @XmlElement(name="ism_transition")
    private IsmTransition ismTransition;
    @XmlElement(name="instruction_details")
    @Nullable
    private InstructionDetails instructionDetails;

    public Action() {
    }

    public Action(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, CodePhrase language, CodePhrase encoding, PartyProxy subject, @Nullable PartyProxy provider, @Nullable ObjectRef workflowId, @Nullable List<Participation> otherParticipations, @Nullable ItemStructure protocol, @Nullable ObjectRef guidelineId, DvDateTime time, ItemStructure description, IsmTransition ismTransition, @Nullable InstructionDetails instructionDetails) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName, language, encoding, subject, provider, workflowId, otherParticipations, protocol, guidelineId);
        this.time = time;
        this.description = description;
        this.ismTransition = ismTransition;
        this.instructionDetails = instructionDetails;
    }

    public DvDateTime getTime() {
        return time;
    }

    public void setTime(DvDateTime time) {
        this.time = time;
    }

    public ItemStructure getDescription() {
        return description;
    }

    public void setDescription(ItemStructure description) {
        this.description = description;
        setThisAsParent(description, "description");
    }
    
    public IsmTransition getIsmTransition() {
        return ismTransition;
    }

    public void setIsmTransition(IsmTransition ismTransition) {
        this.ismTransition = ismTransition;
        setThisAsParent(ismTransition, "ism_transition");
    }
    
    public InstructionDetails getInstructionDetails() {
        return instructionDetails;
    }

    public void setInstructionDetails(InstructionDetails instructionDetails) {
        this.instructionDetails = instructionDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Action action = (Action) o;

        if (time != null ? !time.equals(action.time) : action.time != null) return false;
        if (description != null ? !description.equals(action.description) : action.description != null) return false;
        if (ismTransition != null ? !ismTransition.equals(action.ismTransition) : action.ismTransition != null)
            return false;
        return instructionDetails != null ? instructionDetails.equals(action.instructionDetails) : action.instructionDetails == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (ismTransition != null ? ismTransition.hashCode() : 0);
        result = 31 * result + (instructionDetails != null ? instructionDetails.hashCode() : 0);
        return result;
    }
}
