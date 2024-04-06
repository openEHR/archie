package org.s2.rm.entity.social_entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.interval.Interval;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDate;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Persona
* BMM ancestors: Party
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Persona", propOrder = {
  "uid",
  "performer",
  "timeValidity",
  "capabilities"
})
public class Persona extends Party {
  /**
  * BMM name: performer | BMM type: Object_ref
  * isMandatory: true | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "performer")
  private ObjectRef performer;

  /**
  * BMM name: time_validity | BMM type: Interval<Date>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "time_validity")
  private @Nullable Interval<RmDate> timeValidity;

  /**
  * BMM name: capabilities | BMM type: List<Capability>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "capabilities")
  private @Nullable List<Capability> capabilities;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Persona() {}

  public Persona(ObjectRef performer, List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(identities, archetypeNodeId, name);
    this.performer = performer;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Persona otherAsPersona = (Persona) other;
    return Objects.equals(getLegalStatus(), otherAsPersona.getLegalStatus()) &&
      Objects.equals(getIdentities(), otherAsPersona.getIdentities()) &&
      Objects.equals(getContacts(), otherAsPersona.getContacts()) &&
      Objects.equals(getAccountabilityTypes(), otherAsPersona.getAccountabilityTypes()) &&
      Objects.equals(getDescription(), otherAsPersona.getDescription()) &&
      Objects.equals(uid, otherAsPersona.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsPersona.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsPersona.getName()) &&
      Objects.equals(getCode(), otherAsPersona.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsPersona.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsPersona.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsPersona.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsPersona.getLinks()) &&
      Objects.equals(performer, otherAsPersona.performer) &&
      Objects.equals(timeValidity, otherAsPersona.timeValidity) &&
      Objects.equals(capabilities, otherAsPersona.capabilities);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, performer, timeValidity);
    result = capabilities == null ? 0 : 31 * result + capabilities.hashCode();
    return result;
  }

  public ObjectRef getPerformer() {
    return performer;
  }

  public void setPerformer(ObjectRef performer) {
    this.performer = performer;
  }

  public @Nullable Interval<RmDate> getTimeValidity() {
    return timeValidity;
  }

  public void setTimeValidity(@Nullable Interval<RmDate> timeValidity) {
    this.timeValidity = timeValidity;
  }

  public @Nullable List<Capability> getCapabilities() {
    return capabilities;
  }

  public void setCapabilities(@Nullable List<Capability> capabilities) {
    this.capabilities = capabilities;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Persona";
  }

  @Override
  public String toString() {
    return "Persona";
  }
}
