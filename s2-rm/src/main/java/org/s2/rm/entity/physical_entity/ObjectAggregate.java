package org.s2.rm.entity.physical_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Object_aggregate
* BMM ancestors: Material_entity
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Object_aggregate", propOrder = {
  "uid"
})
public class ObjectAggregate extends MaterialEntity {

  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public ObjectAggregate() {}

  public ObjectAggregate(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ObjectAggregate otherAsObjectAggregate = (ObjectAggregate) other;
    return Objects.equals(getDescription(), otherAsObjectAggregate.getDescription()) &&
      Objects.equals(uid, otherAsObjectAggregate.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsObjectAggregate.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsObjectAggregate.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsObjectAggregate.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsObjectAggregate.getFeederAudit());
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
    return "Object_aggregate";
  }

  @Override
  public String toString() {
    return "Object_aggregate";
  }
}
