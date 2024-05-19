package org.openehr.rm.generic;

import org.openehr.rm.datavalues.DvIdentifier;
import org.openehr.rm.support.identification.PartyRef;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 01/03/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PARTY_IDENTIFIED", propOrder = {
        "name",
        "identifiers"
})
public class PartyIdentified extends PartyProxy {
    @Nullable
    private String name;
    @Nullable
    private List<DvIdentifier> identifiers = new ArrayList<>();

    public PartyIdentified() {
    }

    public PartyIdentified(@Nullable PartyRef externalRef, @Nullable String name, @Nullable List<DvIdentifier> identifiers) {
        super(externalRef);
        this.name = name;
        this.identifiers = identifiers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DvIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<DvIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public void addIdentifier(DvIdentifier identifier) {
        identifiers.add(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PartyIdentified that = (PartyIdentified) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(identifiers, that.identifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, identifiers);
    }

    @Invariant("Basic_validity")
    public boolean basicValidity() {
        return name != null || (identifiers != null && !identifiers.isEmpty()) || getExternalRef() != null;
    }

    @Invariant("Name_valid")
    public boolean nameValid() {
        return InvariantUtil.nullOrNotEmpty(name);
    }

    @Invariant(value = "Identifiers_valid", ignored = true)
    public boolean identifiersValid() {
        return InvariantUtil.nullOrNotEmpty(identifiers);
    }
}
