package org.openehr.rm.demographic;

import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.datastructures.ItemStructure;
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
@XmlType(name="CAPABILITY")
public class Capability extends Locatable {

    private ItemStructure credentials;
    @Nullable
    @XmlElement(name="time_validity")
    private DvInterval<DvDate> timeValidity;

    public ItemStructure getCredentials() {
        return credentials;
    }

    public void setCredentials(ItemStructure credentials) {
        this.credentials = credentials;
        this.setThisAsParent(credentials, "credentials");
    }

    @Nullable
    public DvInterval<DvDate> getTimeValidity() {
        return timeValidity;
    }

    public void setTimeValidity(@Nullable DvInterval<DvDate> timeValidity) {
        this.timeValidity = timeValidity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Capability that = (Capability) o;
        return Objects.equals(credentials, that.credentials) &&
                Objects.equals(timeValidity, that.timeValidity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), credentials, timeValidity);
    }
}
