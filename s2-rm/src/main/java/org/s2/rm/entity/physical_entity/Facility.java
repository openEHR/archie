package org.s2.rm.entity.physical_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Facility
* BMM ancestors: Object_aggregate
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Facility", propOrder = {
  "uid"
})
public class Facility extends ObjectAggregate {

  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Facility() {}

  public Facility(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Facility otherAsFacility = (Facility) other;
    return Objects.equals(getDescription(), otherAsFacility.getDescription()) &&
      Objects.equals(uid, otherAsFacility.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsFacility.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsFacility.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsFacility.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsFacility.getFeederAudit());
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
    return "Facility";
  }

  @Override
  public String toString() {
    return "Facility";
  }
}
