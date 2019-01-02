package com.nedap.archie.rm.demographic;

import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.LocatableRef;

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
@XmlType(name="PARTY")
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
    public DvText getType() {
        return getName();
    }
}
