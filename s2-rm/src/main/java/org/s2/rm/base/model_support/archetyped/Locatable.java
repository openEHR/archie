package org.s2.rm.base.model_support.archetyped;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Locatable
* BMM ancestors: Pathable
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Locatable", propOrder = {
  "archetypeNodeId",
  "name",
  "code",
  "originalCode",
  "archetypeDetails",
  "feederAudit",
  "links"
})
public abstract class Locatable extends Pathable {
  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  // This property is in at least one descendant where it probably has a different type.
  // Skip the property in the parent class (this one).
  // private @Nullable Uuid uid;

  /**
  * BMM name: archetype_node_id | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "archetype_node_id")
  private String archetypeNodeId;

  /**
  * BMM name: name | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "name")
  private String name;

  /**
  * BMM name: code | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "code")
  private @Nullable TerminologyTerm code;

  /**
  * BMM name: original_code | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "original_code")
  private @Nullable TerminologyTerm originalCode;

  /**
  * BMM name: archetype_details | BMM type: Archetyped
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "archetype_details")
  private @Nullable Archetyped archetypeDetails;

  /**
  * BMM name: feeder_audit | BMM type: Feeder_audit
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "feeder_audit")
  private @Nullable FeederAudit feederAudit;

  /**
  * BMM name: links | BMM type: List<Link>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "links")
  private @Nullable List<Link> links;

  public Locatable() {}

  public Locatable(String archetypeNodeId, String name) {
    this.archetypeNodeId = archetypeNodeId;
    this.name = name;
  }

  public String getArchetypeNodeId() {
    return archetypeNodeId;
  }

  public void setArchetypeNodeId(String archetypeNodeId) {
    this.archetypeNodeId = archetypeNodeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public @Nullable TerminologyTerm getCode() {
    return code;
  }

  public void setCode(@Nullable TerminologyTerm code) {
    this.code = code;
  }

  public @Nullable TerminologyTerm getOriginalCode() {
    return originalCode;
  }

  public void setOriginalCode(@Nullable TerminologyTerm originalCode) {
    this.originalCode = originalCode;
  }

  public @Nullable Archetyped getArchetypeDetails() {
    return archetypeDetails;
  }

  public void setArchetypeDetails(@Nullable Archetyped archetypeDetails) {
    this.archetypeDetails = archetypeDetails;
  }

  public @Nullable FeederAudit getFeederAudit() {
    return feederAudit;
  }

  public void setFeederAudit(@Nullable FeederAudit feederAudit) {
    this.feederAudit = feederAudit;
  }

  public @Nullable List<Link> getLinks() {
    return links;
  }

  public void setLinks(@Nullable List<Link> links) {
    this.links = links;
  }

  @Override
  public String bmmClassName() {
    return "Locatable";
  }

  @Override
  public String toString() {
    return "Locatable";
  }
}
