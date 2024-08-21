package org.s2.rm.entity.physical_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Space
* BMM ancestors: Object_extension_part
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Space", propOrder = {
  "parts",
  "uid"
})
public class Space extends ObjectExtensionPart {
  /**
  * BMM name: parts | BMM type: {@code List<Space>}
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "parts")
  private @Nullable List<Space> parts;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Space() {}

  public Space(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Space otherAsSpace = (Space) other;
    return Objects.equals(parts, otherAsSpace.parts) &&
      Objects.equals(getDescription(), otherAsSpace.getDescription()) &&
      Objects.equals(uid, otherAsSpace.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsSpace.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsSpace.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsSpace.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsSpace.getFeederAudit());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid);
    result = parts == null ? 0 : 31 * result + parts.hashCode();
    return result;
  }

  public @Nullable List<Space> getParts() {
    return parts;
  }

  public void setParts(@Nullable List<Space> parts) {
    this.parts = parts;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Space";
  }

  @Override
  public String toString() {
    return "Space";
  }
}
