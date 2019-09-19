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
import java.util.Objects;

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

        EventContext that = (EventContext) o;

        return Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(location, that.location) &&
                Objects.equals(setting, that.setting) &&
                Objects.equals(otherContext, that.otherContext) &&
                Objects.equals(healthCareFacility, that.healthCareFacility) &&
                Objects.equals(participations, that.participations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, location, setting, otherContext, healthCareFacility, participations);
    }
}
