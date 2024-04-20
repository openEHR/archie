package org.openehr.rm.composition;


import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.encapsulated.DvParsable;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.generic.Participation;
import org.openehr.rm.generic.PartyProxy;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
