package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.archetyped.*;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.encapsulated.DvParsable;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmutil.InvariantUtil;

import javax.annotation.Nullable;

import jakarta.xml.bind.annotation.*;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;


/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlRootElement(name = "activity", namespace = "http://schemas.openehr.org/v1" )
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ACTIVITY", propOrder = {
        "description",
        "timing",
        "actionArchetypeId"
})
public class Activity extends Locatable {

    @XmlElement(name = "description", required = true)
    private ItemStructure description;

    @Nullable
    @XmlElement(name = "timing")
    private DvParsable timing;

    @XmlElement(name = "action_archetype_id")
    private String actionArchetypeId;

    public Activity() {
    }

    public Activity(String archetypeNodeId, DvText name, ItemStructure description, @Nullable DvParsable timing, String actionArchetypeId) {
        super(archetypeNodeId, name);
        this.description = description;
        this.timing = timing;
        this.actionArchetypeId = actionArchetypeId;
    }

    public Activity(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, ItemStructure description, @Nullable DvParsable timing, String actionArchetypeId) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.description = description;
        this.timing = timing;
        this.actionArchetypeId = actionArchetypeId;
    }

    public ItemStructure getDescription() {
        return description;
    }

    public void setDescription(ItemStructure description) {
        this.description = description;
        setThisAsParent(description, "description");
    }

    public DvParsable getTiming() {
        return timing;
    }

    public void setTiming(DvParsable timing) {
        this.timing = timing;
    }
    
    public String getActionArchetypeId() {
        return actionArchetypeId;
    }

    public void setActionArchetypeId( String actionArchetypeId) {
        this.actionArchetypeId = actionArchetypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Activity activity = (Activity) o;
        return Objects.equals(description, activity.description) &&
                Objects.equals(timing, activity.timing) &&
                Objects.equals(actionArchetypeId, activity.actionArchetypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, timing, actionArchetypeId);
    }

    @Invariant("Action_archetype_id_valid")
    public boolean actionArchetypeIdValid() {
        return InvariantUtil.nullOrNotEmpty(actionArchetypeId);
    }
}
