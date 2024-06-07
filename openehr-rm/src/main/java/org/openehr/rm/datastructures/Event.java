package org.openehr.rm.datastructures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.datavalues.quantity.datetime.DvDuration;
import org.openehr.rm.support.identification.UIDBasedId;
import org.openehr.rm.archetyped.*;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 03/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EVENT", propOrder = {
        "time",
        "data",
        "state"
})
public abstract class Event<Type extends ItemStructure> extends Locatable {

    private DvDateTime time;
    @Nullable
    private Type state;

    private Type data;


    public Event() {
    }

    public Event(String archetypeNodeId, DvText name, DvDateTime time, Type data) {
        super(archetypeNodeId, name);
        this.time = time;
        this.data = data;
    }


    public Event(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, DvDateTime time, Type data, @Nullable Type state) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.time = time;
        this.state = state;
        this.data = data;
    }

    public DvDateTime getTime() {
        return time;
    }

    public void setTime(DvDateTime time) {
        this.time = time;
    }

    @Nullable
    public Type getState() {
        return state;
    }

    public void setState(@Nullable Type state) {
        this.state = state;
        setThisAsParent(state, "state");
    }

    public Type getData() {
        return data;
    }

    public void setData(Type data) {
        this.data = data;
        setThisAsParent(data, "data");
    }

    @JsonIgnore
    public DvDuration getOffset() {
        DvDuration result = new DvDuration();
        Duration duration = Duration.between(OffsetDateTime.from(((History<?>) getParent()).getOrigin().getValue()), OffsetDateTime.from(time.getValue()));
        result.setValue(duration);
        //would be even better if we could set the accurary too. Let's not for now
        return result;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Event<?> event = (Event<?>) o;
        return Objects.equals(time, event.time) &&
                Objects.equals(state, event.state) &&
                Objects.equals(data, event.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), time, state, data);
    }
}
