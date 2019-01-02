package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.IsmTransition;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
}
