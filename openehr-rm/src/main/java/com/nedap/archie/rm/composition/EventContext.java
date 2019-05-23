package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyIdentified;

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
@XmlType(name = "EVENT_CONTEXT", propOrder = {
        "startTime",
        "endTime",
        "location",
        "setting",
        "otherContext",
        "healthCareFacility",
        "participations"
})
public class EventContext extends Pathable {

    @XmlElement(name = "start_time")
    private DvDateTime startTime;
    @Nullable
    @XmlElement(name = "end_time")
    private DvDateTime endTime;
    @Nullable
    private String location;
    private DvCodedText setting;
    @XmlElement(name = "other_context")
    @Nullable
    private ItemStructure otherContext;

    @Nullable
    @XmlElement(name = "health_care_facility")
    private PartyIdentified healthCareFacility;

    @Nullable
    private List<Participation> participations = new ArrayList<>();

    public EventContext() {
    }

    public EventContext(DvDateTime startTime, DvCodedText setting) {
        this.startTime = startTime;
        this.setting = setting;
    }

    public EventContext(@Nullable PartyIdentified healthCareFacility, DvDateTime startTime, @Nullable DvDateTime endTime, @Nullable List<Participation> participations, @Nullable String location, DvCodedText setting, @Nullable ItemStructure otherContext) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.setting = setting;
        this.otherContext = otherContext;
        this.healthCareFacility = healthCareFacility;
        this.participations = participations;
    }

    public DvDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DvDateTime startTime) {
        this.startTime = startTime;
    }


    public DvDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DvDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DvCodedText getSetting() {
        return setting;
    }

    public void setSetting(DvCodedText setting) {
        this.setting = setting;
    }

    public ItemStructure getOtherContext() {
        return otherContext;
    }

    public void setOtherContext(ItemStructure otherContext) {
        this.otherContext = otherContext;
        setThisAsParent(otherContext, "other_context");
    }

    public PartyIdentified getHealthCareFacility() {
        return healthCareFacility;
    }

    public void setHealthCareFacility(PartyIdentified healthCareFacility) {
        this.healthCareFacility = healthCareFacility;
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<Participation> participations) {
        this.participations = participations;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EventContext that = (EventContext) o;

        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (setting != null ? !setting.equals(that.setting) : that.setting != null) return false;
        if (otherContext != null ? !otherContext.equals(that.otherContext) : that.otherContext != null) return false;
        if (healthCareFacility != null ? !healthCareFacility.equals(that.healthCareFacility) : that.healthCareFacility != null)
            return false;
        return participations != null ? participations.equals(that.participations) : that.participations == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (setting != null ? setting.hashCode() : 0);
        result = 31 * result + (otherContext != null ? otherContext.hashCode() : 0);
        result = 31 * result + (healthCareFacility != null ? healthCareFacility.hashCode() : 0);
        result = 31 * result + (participations != null ? participations.hashCode() : 0);
        return result;
    }
}
