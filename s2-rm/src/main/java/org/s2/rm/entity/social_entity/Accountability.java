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
* BMM name: Accountability
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Accountability", propOrder = {
  "uid",
  "description"
})
public class Accountability extends Locatable {
  /**
  * BMM name: description | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "description")
  private @Nullable List<Node> description;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Accountability() {}

  public Accountability(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Accountability otherAsAccountability = (Accountability) other;
    return Objects.equals(uid, otherAsAccountability.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsAccountability.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsAccountability.getName()) &&
      Objects.equals(getCode(), otherAsAccountability.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsAccountability.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsAccountability.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsAccountability.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsAccountability.getLinks()) &&
      Objects.equals(description, otherAsAccountability.description);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid);
    result = description == null ? 0 : 31 * result + description.hashCode();
    return result;
  }

  public @Nullable List<Node> getDescription() {
    return description;
  }

  public void setDescription(@Nullable List<Node> description) {
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
    return "Accountability";
  }

  @Override
  public String toString() {
    return "Accountability";
  }
}
