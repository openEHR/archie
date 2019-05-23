package com.nedap.archie.rm.generic;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.support.identification.PartyRef;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 * TODO: move to correct package
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PARTY_PROXY", propOrder = {
        "externalRef"
})
public abstract class PartyProxy extends RMObject {

    @Nullable
    @XmlElement(name = "external_ref")
    private PartyRef externalRef;

    public PartyProxy() {
    }

    public PartyProxy(@Nullable PartyRef externalRef) {
        this.externalRef = externalRef;
    }

    public PartyRef getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(PartyRef externalRef) {
        this.externalRef = externalRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartyProxy that = (PartyProxy) o;

        return externalRef != null ? externalRef.equals(that.externalRef) : that.externalRef == null;

    }

    @Override
    public int hashCode() {
        return externalRef != null ? externalRef.hashCode() : 0;
    }
}
