package org.openehr.rm.generic;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.support.identification.PartyRef;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

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
        return Objects.equals(externalRef, that.externalRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalRef);
    }
}
