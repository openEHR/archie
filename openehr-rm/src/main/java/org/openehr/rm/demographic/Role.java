package org.openehr.rm.demographic;

import org.openehr.rm.support.identification.PartyRef;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.datetime.DvDate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ROLE", propOrder = {
        "capabilities",
        "timeValidity",
        "performer"
})
public class Role extends Party {

    @Nullable
    @XmlElement(name="time_validity")
    private DvInterval<DvDate> timeValidity;
    private PartyRef performer;
    @Nullable
    private List<Capability> capabilities;

    @Nullable
    public DvInterval<DvDate> getTimeValidity() {
        return timeValidity;
    }

    public void setTimeValidity(@Nullable DvInterval<DvDate> timeValidity) {
        this.timeValidity = timeValidity;
    }

    public PartyRef getPerformer() {
        return performer;
    }

    public void setPerformer(PartyRef performer) {
        this.performer = performer;
    }

    @Nullable
    public List<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(@Nullable List<Capability> capabilities) {
        this.capabilities = capabilities;
        this.setThisAsParent(capabilities, "capabilities");
    }

    public void addCapability(Capability capability) {
        this.capabilities.add(capability);
        this.setThisAsParent(capability, "capabilities");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Role role = (Role) o;
        return Objects.equals(timeValidity, role.timeValidity) &&
                Objects.equals(performer, role.performer) &&
                Objects.equals(capabilities, role.capabilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timeValidity, performer, capabilities);
    }
}
