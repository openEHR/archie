package org.openehr.rm.datastructures;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.datavalues.quantity.datetime.DvDuration;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HISTORY", propOrder = {
        "origin",
        "period",
        "duration",
        "events",
        "summary"
})
public final class History<Type extends ItemStructure> extends DataStructure {

    private DvDateTime origin;
    @Nullable
    private DvDuration period;
    @Nullable
    private DvDuration duration;

    @Nullable
    private Type summary;

    @Nullable
    private List<Event<Type>> events = new ArrayList<>();


    public History() {
    }

    public History(String archetypeNodeId, DvText name, DvDateTime origin, @Nullable List<Event<Type>> events) {
        super(archetypeNodeId, name);
        this.origin = origin;
        this.events = events;
    }

    public History(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, DvDateTime origin, @Nullable List<Event<Type>> events, @Nullable DvDuration period, @Nullable DvDuration duration, @Nullable Type summary) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.origin = origin;
        this.period = period;
        this.duration = duration;
        this.summary = summary;
        this.events = events;
    }

    public DvDateTime getOrigin() {
        return origin;
    }

    public void setOrigin(DvDateTime origin) {
        this.origin = origin;
    }

    @Nullable
    public DvDuration getPeriod() {
        return period;
    }

    public void setPeriod(@Nullable DvDuration period) {
        this.period = period;
    }

    @Nullable
    public DvDuration getDuration() {
        return duration;
    }

    public void setDuration(@Nullable DvDuration duration) {
        this.duration = duration;
    }

    public List<Event<Type>> getEvents() {
        return events;
    }

    public void setEvents(List<Event<Type>> events) {
        this.events = events;
        setThisAsParent(events, "events");
    }

    public void addEvent(Event<Type> event) {
        events.add(event);
        setThisAsParent(event, "events");
    }

    @Nullable
    public Type getSummary() {
        return summary;
    }

    public void setSummary(@Nullable Type summary) {
        this.summary = summary;
        setThisAsParent(summary, "summary");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        History<?> history = (History<?>) o;
        return Objects.equals(origin, history.origin) &&
                Objects.equals(period, history.period) &&
                Objects.equals(duration, history.duration) &&
                Objects.equals(summary, history.summary) &&
                Objects.equals(events, history.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), origin, period, duration, summary, events);
    }

    @Invariant("Events_valid")
    public boolean eventsValid() {
        return (events != null && !events.isEmpty()) || summary != null;
    }


    @Invariant(value = "Periodic_validity", ignored = true)
    public boolean periodicValid() {
        return true; //not data validation
    }

//TODO    @Invariant("Period_consistency")
//    public boolean periodConsistency() {
//        if(period != null) {
//            period.getValue().
//        }
//        return true;
//    }


}
