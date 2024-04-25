package org.s2.rm.base.model_support.archetyped;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.model_support.identification.ArchetypeId;

/**
* BMM name: Archetyped
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Archetyped", propOrder = {
  "archetypeId",
  "templateId",
  "rmVersion"
})
public class Archetyped extends RMObject {
  /**
  * BMM name: archetype_id | BMM type: Archetype_id
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "archetype_id")
  private ArchetypeId archetypeId;

  /**
  * BMM name: template_id | BMM type: Archetype_id
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "template_id")
  private @Nullable ArchetypeId templateId;

  /**
  * BMM name: rm_version | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "rm_version")
  private String rmVersion;

  public Archetyped() {}

  public Archetyped(ArchetypeId archetypeId, String rmVersion) {
    this.archetypeId = archetypeId;
    this.rmVersion = rmVersion;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Archetyped otherAsArchetyped = (Archetyped) other;
    return Objects.equals(archetypeId, otherAsArchetyped.archetypeId) &&
      Objects.equals(templateId, otherAsArchetyped.templateId) &&
      Objects.equals(rmVersion, otherAsArchetyped.rmVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), archetypeId, templateId, rmVersion);
  }

  public ArchetypeId getArchetypeId() {
    return archetypeId;
  }

  public void setArchetypeId(ArchetypeId archetypeId) {
    this.archetypeId = archetypeId;
  }

  public @Nullable ArchetypeId getTemplateId() {
    return templateId;
  }

  public void setTemplateId(@Nullable ArchetypeId templateId) {
    this.templateId = templateId;
  }

  public String getRmVersion() {
    return rmVersion;
  }

  public void setRmVersion(String rmVersion) {
    this.rmVersion = rmVersion;
  }

  public String bmmClassName() {
    return "Archetyped";
  }

  @Override
  public String toString() {
    return "Archetyped";
  }
}
