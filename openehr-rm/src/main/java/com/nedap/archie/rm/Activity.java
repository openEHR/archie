package com.nedap.archie.rm;

import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datavalues.encapsulated.DvParsable;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ACTIVITY", propOrder = {
        "description",
        "timing",
        "actionArchetypeId"
})
public class Activity extends Locatable {

    private ItemStructure description;
    @Nullable
    private DvParsable timing;
    @Nullable
    @XmlElement(name = "action_archetype_id", required = true)
    private String actionArchetypeId;

    public ItemStructure getDescription() {
        return description;
    }

    public void setDescription(ItemStructure description) {
        this.description = description;
    }

    public DvParsable getTiming() {
        return timing;
    }

    public void setTiming(DvParsable timing) {
        this.timing = timing;
    }

    @Nullable
    public String getActionArchetypeId() {
        return actionArchetypeId;
    }

    public void setActionArchetypeId(@Nullable String actionArchetypeId) {
        this.actionArchetypeId = actionArchetypeId;
    }
}
