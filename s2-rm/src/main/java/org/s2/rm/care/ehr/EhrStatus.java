package org.s2.rm.care.ehr;


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
import org.s2.rm.base.patterns.participation.PartySelf;

/**
* BMM name: Ehr_status
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ehr_status", propOrder = {
  "uid",
  "subject",
  "isQueryable",
  "isModifiable",
  "otherDetails"
})
public class EhrStatus extends Locatable {
  /**
  * BMM name: subject | BMM type: Party_self
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "subject")
  private PartySelf subject;

  /**
  * BMM name: is_queryable | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "is_queryable")
  private boolean isQueryable;

  /**
  * BMM name: is_modifiable | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "is_modifiable")
  private boolean isModifiable;

  /**
  * BMM name: other_details | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "other_details")
  private @Nullable List<Node> otherDetails;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public EhrStatus() {}

  public EhrStatus(PartySelf subject, boolean isQueryable, boolean isModifiable, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.subject = subject;
    this.isQueryable = isQueryable;
    this.isModifiable = isModifiable;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    EhrStatus otherAsEhrStatus = (EhrStatus) other;
    return Objects.equals(uid, otherAsEhrStatus.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsEhrStatus.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsEhrStatus.getName()) &&
      Objects.equals(getCode(), otherAsEhrStatus.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsEhrStatus.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsEhrStatus.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsEhrStatus.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsEhrStatus.getLinks()) &&
      Objects.equals(subject, otherAsEhrStatus.subject) &&
      Objects.equals(isQueryable, otherAsEhrStatus.isQueryable) &&
      Objects.equals(isModifiable, otherAsEhrStatus.isModifiable) &&
      Objects.equals(otherDetails, otherAsEhrStatus.otherDetails);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, subject, isQueryable, isModifiable);
    result = otherDetails == null ? 0 : 31 * result + otherDetails.hashCode();
    return result;
  }

  public PartySelf getSubject() {
    return subject;
  }

  public void setSubject(PartySelf subject) {
    this.subject = subject;
  }

  public boolean getIsQueryable() {
    return isQueryable;
  }

  public void setIsQueryable(boolean isQueryable) {
    this.isQueryable = isQueryable;
  }

  public boolean getIsModifiable() {
    return isModifiable;
  }

  public void setIsModifiable(boolean isModifiable) {
    this.isModifiable = isModifiable;
  }

  public @Nullable List<Node> getOtherDetails() {
    return otherDetails;
  }

  public void setOtherDetails(@Nullable List<Node> otherDetails) {
    this.otherDetails = otherDetails;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Ehr_status";
  }

  @Override
  public String toString() {
    return "Ehr_status";
  }
}
