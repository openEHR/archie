package main.java.org.s2.rm.base.change_control;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Audit_details
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Audit_details", propOrder = {
  "systemId",
  "timeCommitted",
  "changeType",
  "description",
  "committer"
})
public class AuditDetails {
  /**
  * BMM name: system_id | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "system_id")
  private @Nonnull String systemId;

  /**
  * BMM name: time_committed | BMM type: Date_time
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "time_committed")
  private @Nonnull RmDateTime timeCommitted;

  /**
  * BMM name: change_type | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "change_type")
  private @Nonnull TerminologyTerm changeType;

  /**
  * BMM name: description | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "description")
  private @Nullable TerminologyTerm description;

  /**
  * BMM name: committer | BMM type: Party_proxy
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "committer")
  private @Nonnull PartyProxy committer;

  public AuditDetails() {}

  public AuditDetails(@Nonnull String systemId, @Nonnull RmDateTime timeCommitted, @Nonnull TerminologyTerm changeType, @Nonnull PartyProxy committer) {
    this.systemId = systemId;
    this.timeCommitted = timeCommitted;
    this.changeType = changeType;
    this.committer = committer;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    AuditDetails otherAsAuditDetails = (AuditDetails) other;
    return Objects.equals(systemId, otherAsAuditDetails.systemId) &&
      Objects.equals(timeCommitted, otherAsAuditDetails.timeCommitted) &&
      Objects.equals(changeType, otherAsAuditDetails.changeType) &&
      Objects.equals(description, otherAsAuditDetails.description) &&
      Objects.equals(committer, otherAsAuditDetails.committer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), systemId, timeCommitted, changeType, description, committer);
  }

  public @Nonnull String getSystemId() {
    return systemId;
  }

  public void setSystemId(@Nonnull String systemId) {
    this.systemId = systemId;
  }

  public @Nonnull RmDateTime getTimeCommitted() {
    return timeCommitted;
  }

  public void setTimeCommitted(@Nonnull RmDateTime timeCommitted) {
    this.timeCommitted = timeCommitted;
  }

  public @Nonnull TerminologyTerm getChangeType() {
    return changeType;
  }

  public void setChangeType(@Nonnull TerminologyTerm changeType) {
    this.changeType = changeType;
  }

  public @Nullable TerminologyTerm getDescription() {
    return description;
  }

  public void setDescription(@Nullable TerminologyTerm description) {
    this.description = description;
  }

  public @Nonnull PartyProxy getCommitter() {
    return committer;
  }

  public void setCommitter(@Nonnull PartyProxy committer) {
    this.committer = committer;
  }

  public String bmmClassName() {
    return "Audit_details";
  }

  @Override
  public String toString() {
    return "Audit_details";
  }
}
