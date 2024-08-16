package org.s2.rm.entity.physical_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Independent_object
* BMM ancestors: Object_extension_part
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Independent_object", propOrder = {
  "parts",
  "uid"
})
public class IndependentObject extends ObjectExtensionPart {

  // Properties added from the extended class: ObjectExtensionPart

  /**
  * BMM name: parts | BMM type: List<{@literal Object_extension_part}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "parts")
  private @Nullable List<ObjectExtensionPart> parts;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public IndependentObject() {}

  public IndependentObject(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    IndependentObject otherAsIndependentObject = (IndependentObject) other;
    return Objects.equals(parts, otherAsIndependentObject.parts) &&
      Objects.equals(getDescription(), otherAsIndependentObject.getDescription()) &&
      Objects.equals(uid, otherAsIndependentObject.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsIndependentObject.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsIndependentObject.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsIndependentObject.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsIndependentObject.getFeederAudit());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid);
    result = parts == null ? 0 : 31 * result + parts.hashCode();
    return result;
  }

  public @Nullable List<ObjectExtensionPart> getParts() {
    return parts;
  }

  public void setParts(@Nullable List<ObjectExtensionPart> parts) {
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
    return "Independent_object";
  }

  @Override
  public String toString() {
    return "Independent_object";
  }
}
