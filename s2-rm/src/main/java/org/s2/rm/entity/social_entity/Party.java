package org.s2.rm.entity.social_entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Party
* BMM ancestors: Social_entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party", propOrder = {
  "legalStatus",
  "identities",
  "contacts",
  "accountabilityTypes"
})
public abstract class Party extends SocialEntity {
  /**
  * BMM name: legal_status | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "legal_status")
  private @Nullable String legalStatus;

  /**
  * BMM name: identities | BMM type: List<Party_identity>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "identities")
  private List<PartyIdentity> identities;

  /**
  * BMM name: contacts | BMM type: Set<Contact>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "contacts")
  private @Nullable Set<Contact> contacts;

  /**
  * BMM name: accountability_types | BMM type: List<Accountability>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "accountability_types")
  private @Nullable List<Accountability> accountabilityTypes;

  public Party() {}

  public Party(List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.identities = identities;
  }

  public @Nullable String getLegalStatus() {
    return legalStatus;
  }

  public void setLegalStatus(@Nullable String legalStatus) {
    this.legalStatus = legalStatus;
  }

  public List<PartyIdentity> getIdentities() {
    return identities;
  }

  public void setIdentities(List<PartyIdentity> identities) {
    this.identities = identities;
  }

  public @Nullable Set<Contact> getContacts() {
    return contacts;
  }

  public void setContacts(@Nullable Set<Contact> contacts) {
    this.contacts = contacts;
  }

  public @Nullable List<Accountability> getAccountabilityTypes() {
    return accountabilityTypes;
  }

  public void setAccountabilityTypes(@Nullable List<Accountability> accountabilityTypes) {
    this.accountabilityTypes = accountabilityTypes;
  }

  @Override
  public String bmmClassName() {
    return "Party";
  }

  @Override
  public String toString() {
    return "Party";
  }
}
