package com.nedap.archie.rm.composition;


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
import com.nedap.archie.rm.support.identification.ObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Instruction(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, CodePhrase language, CodePhrase encoding, PartyProxy subject, @Nullable PartyProxy provider, @Nullable ObjectRef<? extends ObjectId> workflowId, @Nullable List<Participation> otherParticipations, @Nullable ItemStructure protocol, @Nullable ObjectRef<? extends ObjectId> guidelineId, DvText narrative, @Nullable List<Activity> activities, @Nullable DvDateTime expiryTime, @Nullable DvParsable wfDefinition) {
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
            setThisAsParent(activity, "activities");
        }
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
        setThisAsParent(activity, "activities");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Instruction that = (Instruction) o;

        return Objects.equals(narrative, that.narrative) &&
                Objects.equals(expiryTime, that.expiryTime) &&
                Objects.equals(wfDefinition, that.wfDefinition) &&
                Objects.equals(activities, that.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), narrative, expiryTime, wfDefinition, activities);
    }

    @Invariant(value="Activities_valid", ignored = true)
    public boolean activitiesValid() {
        return InvariantUtil.nullOrNotEmpty(activities);
    }
}
