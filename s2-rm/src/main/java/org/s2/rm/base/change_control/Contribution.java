package org.s2.rm.base.change_control;


import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
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
public class Contribution extends RMObject {
  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "uid")
  private Uuid uid;

  /**
  * BMM name: audit | BMM type: Audit_details
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "audit")
  private AuditDetails audit;

  /**
  * BMM name: versions | BMM type: List<Object_ref>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "versions")
  private List<ObjectRef> versions;

  public Contribution() {}

  public Contribution(Uuid uid, AuditDetails audit, List<ObjectRef> versions) {
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

  public Uuid getUid() {
    return uid;
  }

  public void setUid(Uuid uid) {
    this.uid = uid;
  }

  public AuditDetails getAudit() {
    return audit;
  }

  public void setAudit(AuditDetails audit) {
    this.audit = audit;
  }

  public List<ObjectRef> getVersions() {
    return versions;
  }

  public void setVersions(List<ObjectRef> versions) {
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
