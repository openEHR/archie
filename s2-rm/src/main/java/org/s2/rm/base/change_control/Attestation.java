package org.s2.rm.base.change_control;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.encapsulated.Multimedia;
import org.s2.rm.base.foundation_types.primitive_types.Uri;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Attestation
* BMM ancestors: Audit_details
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attestation", propOrder = {
  "attestedView",
  "proof",
  "items",
  "reason",
  "isPending"
})
public class Attestation extends AuditDetails {
  /**
  * BMM name: attested_view | BMM type: Multimedia
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "attested_view")
  private @Nullable Multimedia attestedView;

  /**
  * BMM name: proof | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "proof")
  private @Nullable String proof;

  /**
  * BMM name: items | BMM type: List<Uri>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "items")
  private @Nullable List<Uri> items;

  /**
  * BMM name: reason | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "reason")
  private String reason;

  /**
  * BMM name: is_pending | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "is_pending")
  private boolean isPending;

  public Attestation() {}

  public Attestation(String reason, boolean isPending, String systemId, RmDateTime timeCommitted, TerminologyTerm changeType, PartyProxy committer) {
    super(systemId, timeCommitted, changeType, committer);
    this.reason = reason;
    this.isPending = isPending;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Attestation otherAsAttestation = (Attestation) other;
    return Objects.equals(getSystemId(), otherAsAttestation.getSystemId()) &&
      Objects.equals(getTimeCommitted(), otherAsAttestation.getTimeCommitted()) &&
      Objects.equals(getChangeType(), otherAsAttestation.getChangeType()) &&
      Objects.equals(getDescription(), otherAsAttestation.getDescription()) &&
      Objects.equals(getCommitter(), otherAsAttestation.getCommitter()) &&
      Objects.equals(attestedView, otherAsAttestation.attestedView) &&
      Objects.equals(proof, otherAsAttestation.proof) &&
      Objects.equals(items, otherAsAttestation.items) &&
      Objects.equals(reason, otherAsAttestation.reason) &&
      Objects.equals(isPending, otherAsAttestation.isPending);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), attestedView, proof, reason, isPending);
    result = items == null ? 0 : 31 * result + items.hashCode();
    return result;
  }

  public @Nullable Multimedia getAttestedView() {
    return attestedView;
  }

  public void setAttestedView(@Nullable Multimedia attestedView) {
    this.attestedView = attestedView;
  }

  public @Nullable String getProof() {
    return proof;
  }

  public void setProof(@Nullable String proof) {
    this.proof = proof;
  }

  public @Nullable List<Uri> getItems() {
    return items;
  }

  public void setItems(@Nullable List<Uri> items) {
    this.items = items;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public boolean getIsPending() {
    return isPending;
  }

  public void setIsPending(boolean isPending) {
    this.isPending = isPending;
  }

  @Override
  public String bmmClassName() {
    return "Attestation";
  }

  @Override
  public String toString() {
    return "Attestation";
  }
}
