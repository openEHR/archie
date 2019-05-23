package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.Activity;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.encapsulated.DvParsable;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyProxy;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 03/11/15.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "INSTRUCTION", propOrder = {
        "narrative",
        "expiryTime",
        "wfDefinition",
        "activities"
})
public class Instruction extends CareEntry {
    private DvText narrative;
    @Nullable
    @XmlElement(name = "expiry_time")
    private DvDateTime expiryTime;
    @Nullable
    @XmlElement(name = "wf_definition")
    private DvParsable wfDefinition;
    @Nullable
    private List<Activity> activities = new ArrayList<>();

    public Instruction() {
    }

    public Instruction(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, CodePhrase language, CodePhrase encoding, PartyProxy subject, @Nullable PartyProxy provider, @Nullable ObjectRef workflowId, @Nullable List<Participation> otherParticipations, @Nullable ItemStructure protocol, @Nullable ObjectRef guidelineId, DvText narrative, @Nullable List<Activity> activities, @Nullable DvDateTime expiryTime, @Nullable DvParsable wfDefinition) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName, language, encoding, subject, provider, workflowId, otherParticipations, protocol, guidelineId);
        this.narrative = narrative;
        this.expiryTime = expiryTime;
        this.wfDefinition = wfDefinition;
        this.activities = activities;
    }

    public DvText getNarrative() {
        return narrative;
    }

    public void setNarrative(DvText narrative) {
        this.narrative = narrative;
    }

    @Nullable
    public DvDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(@Nullable DvDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Nullable
    public DvParsable getWfDefinition() {
        return wfDefinition;
    }

    public void setWfDefinition(@Nullable DvParsable wfDefinition) {
        this.wfDefinition = wfDefinition;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
        for (Activity activity : activities) {
            setThisAsParent(activity, "activity");
        }
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
        setThisAsParent(activity, "activity");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Instruction that = (Instruction) o;

        if (narrative != null ? !narrative.equals(that.narrative) : that.narrative != null) return false;
        if (expiryTime != null ? !expiryTime.equals(that.expiryTime) : that.expiryTime != null) return false;
        if (wfDefinition != null ? !wfDefinition.equals(that.wfDefinition) : that.wfDefinition != null) return false;
        return activities != null ? activities.equals(that.activities) : that.activities == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (narrative != null ? narrative.hashCode() : 0);
        result = 31 * result + (expiryTime != null ? expiryTime.hashCode() : 0);
        result = 31 * result + (wfDefinition != null ? wfDefinition.hashCode() : 0);
        result = 31 * result + (activities != null ? activities.hashCode() : 0);
        return result;
    }
}
