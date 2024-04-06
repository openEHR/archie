package org.s2.rm.entity.social_entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Party_identity
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party_identity", propOrder = {
  "uid",
  "description"
})
public class PartyIdentity extends Locatable {
  /**
  * BMM name: description | BMM type: List<Node>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "description")
  private List<Node> description;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public PartyIdentity() {}

  public PartyIdentity(List<Node> description, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.description = description;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PartyIdentity otherAsPartyIdentity = (PartyIdentity) other;
    return Objects.equals(uid, otherAsPartyIdentity.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsPartyIdentity.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsPartyIdentity.getName()) &&
      Objects.equals(getCode(), otherAsPartyIdentity.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsPartyIdentity.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsPartyIdentity.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsPartyIdentity.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsPartyIdentity.getLinks()) &&
      Objects.equals(description, otherAsPartyIdentity.description);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid);
    result = description == null ? 0 : 31 * result + description.hashCode();
    return result;
  }

  public List<Node> getDescription() {
    return description;
  }

  public void setDescription(List<Node> description) {
    this.description = description;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Party_identity";
  }

  @Override
  public String toString() {
    return "Party_identity";
  }
}
