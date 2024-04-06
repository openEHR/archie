package org.s2.rm.entity.social_entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Person
* BMM ancestors: Individual_agent
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Person", propOrder = {
  "uid"
})
public class Person extends IndividualAgent {

  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Person() {}

  public Person(List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(identities, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Person otherAsPerson = (Person) other;
    return Objects.equals(getPersonas(), otherAsPerson.getPersonas()) &&
      Objects.equals(getLanguages(), otherAsPerson.getLanguages()) &&
      Objects.equals(getLegalStatus(), otherAsPerson.getLegalStatus()) &&
      Objects.equals(getIdentities(), otherAsPerson.getIdentities()) &&
      Objects.equals(getContacts(), otherAsPerson.getContacts()) &&
      Objects.equals(getAccountabilityTypes(), otherAsPerson.getAccountabilityTypes()) &&
      Objects.equals(getDescription(), otherAsPerson.getDescription()) &&
      Objects.equals(uid, otherAsPerson.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsPerson.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsPerson.getName()) &&
      Objects.equals(getCode(), otherAsPerson.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsPerson.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsPerson.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsPerson.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsPerson.getLinks());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid);
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Person";
  }

  @Override
  public String toString() {
    return "Person";
  }
}
