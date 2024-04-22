package org.openehr.rm.demographic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datavalues.DvText;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PARTY_IDENTITY")
public class PartyIdentity extends Locatable {

    private ItemStructure details;

    @JsonIgnore
    @XmlTransient
    public DvText getPurpose() {
        return getName();
    }

    public ItemStructure getDetails() {
        return details;
    }

    public void setDetails(ItemStructure details) {
        this.details = details;
        setThisAsParent(details, "details");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PartyIdentity that = (PartyIdentity) o;
        return Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), details);
    }
}
