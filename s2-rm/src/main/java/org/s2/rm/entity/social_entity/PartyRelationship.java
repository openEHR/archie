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
import org.s2.rm.entity.EntityRelationship;

/**
* BMM name: Party_relationship
* BMM ancestors: Entity_relationship
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party_relationship", propOrder = {
  "uid",
  "scoper"
})
public class PartyRelationship extends EntityRelationship {
  /**
  * BMM name: scoper | BMM type: Accountability
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "scoper")
  private @Nullable Accountability scoper;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public PartyRelationship() {}

  public PartyRelationship(TerminologyTerm type, ObjectRef source, ObjectRef target, String archetypeNodeId, String name) {
    super(type, source, target, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PartyRelationship otherAsPartyRelationship = (PartyRelationship) other;
    return Objects.equals(getType(), otherAsPartyRelationship.getType()) &&
      Objects.equals(getSource(), otherAsPartyRelationship.getSource()) &&
      Objects.equals(getTarget(), otherAsPartyRelationship.getTarget()) &&
      Objects.equals(getDescription(), otherAsPartyRelationship.getDescription()) &&
      Objects.equals(getTimeValidity(), otherAsPartyRelationship.getTimeValidity()) &&
      Objects.equals(uid, otherAsPartyRelationship.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsPartyRelationship.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsPartyRelationship.getName()) &&
      Objects.equals(getCode(), otherAsPartyRelationship.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsPartyRelationship.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsPartyRelationship.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsPartyRelationship.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsPartyRelationship.getLinks()) &&
      Objects.equals(scoper, otherAsPartyRelationship.scoper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid, scoper);
  }

  public @Nullable Accountability getScoper() {
    return scoper;
  }

  public void setScoper(@Nullable Accountability scoper) {
    this.scoper = scoper;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Party_relationship";
  }

  @Override
  public String toString() {
    return "Party_relationship";
  }
}
