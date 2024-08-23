package org.s2.rm.entity.social_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Address
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Address", propOrder = {
  "uid",
  "description"
})
public class Address extends Locatable {
  /**
  * BMM name: description | BMM type: {@code List<Node>}
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

  public Address() {}

  public Address(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Address otherAsAddress = (Address) other;
    return Objects.equals(uid, otherAsAddress.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsAddress.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsAddress.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsAddress.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsAddress.getFeederAudit()) &&
      Objects.equals(description, otherAsAddress.description);
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
    return "Address";
  }

  @Override
  public String toString() {
    return "Address";
  }
}