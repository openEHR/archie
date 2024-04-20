package org.openehr.rm.demographic;

import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.support.identification.PartyRef;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.datetime.DvDate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PARTY_RELATIONSHIP")
public class PartyRelationship extends Locatable {

    @Nullable
    private ItemStructure details;
    @Nullable
    @XmlElement(name="time_validity")
    private DvInterval<DvDate> timeValidity;

    private PartyRef source;
    private PartyRef target;

    @Nullable
    public ItemStructure getDetails() {
        return details;
    }

    public void setDetails(@Nullable ItemStructure details) {
        this.details = details;
        setThisAsParent(details, "details");
    }

    @Nullable
    public DvInterval<DvDate> getTimeValidity() {
        return timeValidity;
    }

    public void setTimeValidity(@Nullable DvInterval<DvDate> timeValidity) {
        this.timeValidity = timeValidity;
    }

    public PartyRef getSource() {
        return source;
    }

    public void setSource(PartyRef source) {
        this.source = source;
    }

    public PartyRef getTarget() {
        return target;
    }

    public void setTarget(PartyRef target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PartyRelationship that = (PartyRelationship) o;
        return Objects.equals(details, that.details) &&
                Objects.equals(timeValidity, that.timeValidity) &&
                Objects.equals(source, that.source) &&
                Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), details, timeValidity, source, target);
    }
}
