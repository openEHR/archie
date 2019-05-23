package com.nedap.archie.rm.generic;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.support.identification.PartyRef;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PARTY_RELATED")
public class PartyRelated extends PartyIdentified {
    private DvCodedText relationship;

    public PartyRelated() {
    }

    public PartyRelated(@Nullable PartyRef externalRef, @Nullable String name, @Nullable List<DvIdentifier> identifiers, DvCodedText relationship) {
        super(externalRef, name, identifiers);
        this.relationship = relationship;
    }

    public DvCodedText getRelationship() {
        return relationship;
    }

    public void setRelationship(DvCodedText relationship) {
        this.relationship = relationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PartyRelated that = (PartyRelated) o;

        return relationship != null ? relationship.equals(that.relationship) : that.relationship == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (relationship != null ? relationship.hashCode() : 0);
        return result;
    }
}
