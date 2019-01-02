package com.nedap.archie.rm.demographic;

import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CONTACT")
public class Contact extends Locatable {

    @Nullable
    private List<Address> addresses = new ArrayList<>();

    @Nullable
    @XmlElement(name="time_validity")
    private DvInterval<DvDate> timeValidity;

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
}
