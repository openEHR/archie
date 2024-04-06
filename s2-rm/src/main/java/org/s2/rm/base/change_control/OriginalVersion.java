package org.s2.rm.base.change_control;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.ObjectVersionId;

/**
* BMM name: Original_version
* BMM generic parameters: Original_version<T Any>
* BMM ancestors: Version<T>
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Original_version", propOrder = {
  "uid",
  "precedingVersionUid",
  "otherInputVersionUids",
  "attestations",
  "lifecycleState",
  "data"
})
public class OriginalVersion<T> extends Version<T> {
  /**
  * BMM name: uid | BMM type: Object_version_id
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "uid")
  private ObjectVersionId uid;

  /**
  * BMM name: preceding_version_uid | BMM type: Object_version_id
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "preceding_version_uid")
  private @Nullable ObjectVersionId precedingVersionUid;

  /**
  * BMM name: other_input_version_uids | BMM type: List<Object_version_id>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "other_input_version_uids")
  private @Nullable List<ObjectVersionId> otherInputVersionUids;

  /**
  * BMM name: attestations | BMM type: List<Attestation>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "attestations")
  private @Nullable List<Attestation> attestations;

  /**
  * BMM name: lifecycle_state | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "lifecycle_state")
  private TerminologyTerm lifecycleState;

  /**
  * BMM name: data | BMM type: T
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "data")
  private @Nullable T data;

  public OriginalVersion() {}

  public OriginalVersion(ObjectVersionId uid, TerminologyTerm lifecycleState, ObjectRef contribution, AuditDetails commitAudit) {
    super(contribution, commitAudit);
    this.uid = uid;
    this.lifecycleState = lifecycleState;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    OriginalVersion<T> otherAsOriginalVersion = (OriginalVersion<T>) other;
    return Objects.equals(getContribution(), otherAsOriginalVersion.getContribution()) &&
      Objects.equals(getCommitAudit(), otherAsOriginalVersion.getCommitAudit()) &&
      Objects.equals(getSignature(), otherAsOriginalVersion.getSignature()) &&
      Objects.equals(uid, otherAsOriginalVersion.uid) &&
      Objects.equals(precedingVersionUid, otherAsOriginalVersion.precedingVersionUid) &&
      Objects.equals(otherInputVersionUids, otherAsOriginalVersion.otherInputVersionUids) &&
      Objects.equals(attestations, otherAsOriginalVersion.attestations) &&
      Objects.equals(lifecycleState, otherAsOriginalVersion.lifecycleState) &&
      Objects.equals(data, otherAsOriginalVersion.data);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, precedingVersionUid, lifecycleState, data);
    result = otherInputVersionUids == null ? 0 : 31 * result + otherInputVersionUids.hashCode();
    result = attestations == null ? 0 : 31 * result + attestations.hashCode();
    return result;
  }

  public ObjectVersionId getUid() {
    return uid;
  }

  public void setUid(ObjectVersionId uid) {
    this.uid = uid;
  }

  public @Nullable ObjectVersionId getPrecedingVersionUid() {
    return precedingVersionUid;
  }

  public void setPrecedingVersionUid(@Nullable ObjectVersionId precedingVersionUid) {
    this.precedingVersionUid = precedingVersionUid;
  }

  public @Nullable List<ObjectVersionId> getOtherInputVersionUids() {
    return otherInputVersionUids;
  }

  public void setOtherInputVersionUids(@Nullable List<ObjectVersionId> otherInputVersionUids) {
    this.otherInputVersionUids = otherInputVersionUids;
  }

  public @Nullable List<Attestation> getAttestations() {
    return attestations;
  }

  public void setAttestations(@Nullable List<Attestation> attestations) {
    this.attestations = attestations;
  }

  public TerminologyTerm getLifecycleState() {
    return lifecycleState;
  }

  public void setLifecycleState(TerminologyTerm lifecycleState) {
    this.lifecycleState = lifecycleState;
  }

  public @Nullable T getData() {
    return data;
  }

  public void setData(@Nullable T data) {
    this.data = data;
  }

  @Override
  public String bmmClassName() {
    return "Original_version";
  }

  @Override
  public String toString() {
    return "Original_version";
  }
}
