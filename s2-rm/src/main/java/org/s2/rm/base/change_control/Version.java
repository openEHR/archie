package main.java.org.s2.rm.base.change_control;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.identification.ObjectRef;

/**
* BMM name: Version
* BMM generic parameters: Version<T Any>
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Version", propOrder = {
  "contribution",
  "commitAudit",
  "signature"
})
public abstract class Version<T> {
  /**
  * BMM name: contribution | BMM type: Object_ref
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "contribution")
  private @Nonnull ObjectRef contribution;

  /**
  * BMM name: commit_audit | BMM type: Audit_details
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "commit_audit")
  private @Nonnull AuditDetails commitAudit;

  /**
  * BMM name: signature | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "signature")
  private @Nullable String signature;

  public Version() {}

  public Version(@Nonnull ObjectRef contribution, @Nonnull AuditDetails commitAudit) {
    this.contribution = contribution;
    this.commitAudit = commitAudit;
  }

  public @Nonnull ObjectRef getContribution() {
    return contribution;
  }

  public void setContribution(@Nonnull ObjectRef contribution) {
    this.contribution = contribution;
  }

  public @Nonnull AuditDetails getCommitAudit() {
    return commitAudit;
  }

  public void setCommitAudit(@Nonnull AuditDetails commitAudit) {
    this.commitAudit = commitAudit;
  }

  public @Nullable String getSignature() {
    return signature;
  }

  public void setSignature(@Nullable String signature) {
    this.signature = signature;
  }

  public String bmmClassName() {
    return "Version";
  }

  @Override
  public String toString() {
    return "Version";
  }
}
