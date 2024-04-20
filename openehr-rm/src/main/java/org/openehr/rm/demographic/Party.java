package org.openehr.rm.demographic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.LocatableRef;

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
@XmlType(name="PARTY", propOrder = {
        "identities",
        "contacts",
        "relationships",
        "reverseRelationships",
        "details"
})
public abstract class Party extends Locatable {

    private List<PartyIdentity> identities = new ArrayList<>();
    @Nullable
    private List<Contact> contacts = new ArrayList<>();
    @Nullable
    private ItemStructure details;
    @Nullable
    @XmlElement(name="reverse_relationships")
    private List<LocatableRef> reverseRelationships = new ArrayList<>();
    @Nullable
    private List<PartyRelationship> relationships = new ArrayList<>();

    //TODO: uid is redefined here as HIER_OBJECT_ID...


    public List<PartyIdentity> getIdentities() {
        return identities;
    }

    public void setIdentities(List<PartyIdentity> identities) {
        this.identities = identities;
        setThisAsParent(identities, "identities");
    }

    @Nullable
    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(@Nullable List<Contact> contacts) {
        this.contacts = contacts;
        setThisAsParent(contacts, "contacts");
    }

    public void addContact(Contact contact) {
        this.contacts.add(contact);
        this.setThisAsParent(contact, "contacts");

    }

    @Nullable
    public ItemStructure getDetails() {
        return details;
    }

    public void setDetails(@Nullable ItemStructure details) {
        this.details = details;
        setThisAsParent(details, "details");
    }

    @Nullable
    public List<LocatableRef> getReverseRelationships() {
        return reverseRelationships;
    }

    public void setReverseRelationships(@Nullable List<LocatableRef> reverseRelationships) {
        this.reverseRelationships = reverseRelationships;
    }

    public void addReverseRelationship(LocatableRef reverseRelationship) {
        this.reverseRelationships.add(reverseRelationship);
    }

    @Nullable
    public List<PartyRelationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(@Nullable List<PartyRelationship> relationships) {
        this.relationships = relationships;
        setThisAsParent(relationships, "relationships");
    }

    public void addRelationship(PartyRelationship relationship) {
        this.relationships.add(relationship);
        this.setThisAsParent(relationship, "relationships");
    }

    /**
     * Type of party, such as PERSON, ORGANISATION, etc. Role name, e.g. general practitioner , nurse , private citizen . Taken from inherited name attribute.
     */
    @JsonIgnore
    @XmlTransient
    public DvText getType() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Party party = (Party) o;
        return Objects.equals(identities, party.identities) &&
                Objects.equals(contacts, party.contacts) &&
                Objects.equals(details, party.details) &&
                Objects.equals(reverseRelationships, party.reverseRelationships) &&
                Objects.equals(relationships, party.relationships);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identities, contacts, details, reverseRelationships, relationships);
    }
}
