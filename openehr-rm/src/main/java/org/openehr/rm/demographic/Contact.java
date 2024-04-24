package org.openehr.rm.demographic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.datetime.DvDate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CONTACT", propOrder = {
        "timeValidity",
        "addresses"
})
public class Contact extends Locatable {

    @Nullable
    private List<Address> addresses = new ArrayList<>();

    @Nullable
    @XmlElement(name="time_validity")
    private DvInterval<DvDate> timeValidity;

    @JsonIgnore
    @XmlTransient
    public DvText getPurpose() {
        return getName();
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
        this.setThisAsParent(addresses, "addresses");
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
        this.setThisAsParent(address, "addresses");
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
        Contact contact = (Contact) o;
        return Objects.equals(addresses, contact.addresses) &&
                Objects.equals(timeValidity, contact.timeValidity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), addresses, timeValidity);
    }
}
