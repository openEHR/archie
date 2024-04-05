package main.java.org.s2.rm.base.change_control;

import jakarta.annotation.Nonnull;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Contribution
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Contribution", propOrder = {
  "uid",
  "audit",
  "versions"
})
public class Contribution {
  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "uid")
  private @Nonnull Uuid uid;

  /**
  * BMM name: audit | BMM type: Audit_details
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "audit")
  private @Nonnull AuditDetails audit;

  /**
  * BMM name: versions | BMM type: List<Object_ref>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "versions")
  private @Nonnull List<ObjectRef> versions;

  public Contribution() {}

  public Contribution(@Nonnull Uuid uid, @Nonnull AuditDetails audit, @Nonnull List<ObjectRef> versions) {
    this.uid = uid;
    this.audit = audit;
    this.versions = versions;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Contribution otherAsContribution = (Contribution) other;
    return Objects.equals(uid, otherAsContribution.uid) &&
      Objects.equals(audit, otherAsContribution.audit) &&
      Objects.equals(versions, otherAsContribution.versions);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, audit);
    result = versions == null ? 0 : 31 * result + versions.hashCode();
    return result;
  }

  public @Nonnull Uuid getUid() {
    return uid;
  }

  public void setUid(@Nonnull Uuid uid) {
    this.uid = uid;
  }

  public @Nonnull AuditDetails getAudit() {
    return audit;
  }

  public void setAudit(@Nonnull AuditDetails audit) {
    this.audit = audit;
  }

  public @Nonnull List<ObjectRef> getVersions() {
    return versions;
  }

  public void setVersions(@Nonnull List<ObjectRef> versions) {
    this.versions = versions;
  }

  public String bmmClassName() {
    return "Contribution";
  }

  @Override
  public String toString() {
    return "Contribution";
  }
}
