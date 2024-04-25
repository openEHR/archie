package org.s2.rm.base.model_support.archetyped;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.data_types.RweIdRef;
import org.s2.rm.base.data_types.encapsulated.Encapsulated;

/**
* BMM name: Feeder_audit
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Feeder_audit", propOrder = {
  "originatingSystemItemIds",
  "feederSystemItemIds",
  "originalContent",
  "originatingSystemAudit",
  "feederSystemAudit"
})
public class FeederAudit extends RMObject {
  /**
  * BMM name: originating_system_item_ids | BMM type: List<Rwe_id_ref>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "originating_system_item_ids")
  private @Nullable List<RweIdRef> originatingSystemItemIds;

  /**
  * BMM name: feeder_system_item_ids | BMM type: List<Rwe_id_ref>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "feeder_system_item_ids")
  private @Nullable List<RweIdRef> feederSystemItemIds;

  /**
  * BMM name: original_content | BMM type: Encapsulated
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "original_content")
  private @Nullable Encapsulated originalContent;

  /**
  * BMM name: originating_system_audit | BMM type: Feeder_audit_details
  * isMandatory: true | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "originating_system_audit")
  private FeederAuditDetails originatingSystemAudit;

  /**
  * BMM name: feeder_system_audit | BMM type: Feeder_audit_details
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "feeder_system_audit")
  private @Nullable FeederAuditDetails feederSystemAudit;

  public FeederAudit() {}

  public FeederAudit(FeederAuditDetails originatingSystemAudit) {
    this.originatingSystemAudit = originatingSystemAudit;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    FeederAudit otherAsFeederAudit = (FeederAudit) other;
    return Objects.equals(originatingSystemItemIds, otherAsFeederAudit.originatingSystemItemIds) &&
      Objects.equals(feederSystemItemIds, otherAsFeederAudit.feederSystemItemIds) &&
      Objects.equals(originalContent, otherAsFeederAudit.originalContent) &&
      Objects.equals(originatingSystemAudit, otherAsFeederAudit.originatingSystemAudit) &&
      Objects.equals(feederSystemAudit, otherAsFeederAudit.feederSystemAudit);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), originalContent, originatingSystemAudit, feederSystemAudit);
    result = originatingSystemItemIds == null ? 0 : 31 * result + originatingSystemItemIds.hashCode();
    result = feederSystemItemIds == null ? 0 : 31 * result + feederSystemItemIds.hashCode();
    return result;
  }

  public @Nullable List<RweIdRef> getOriginatingSystemItemIds() {
    return originatingSystemItemIds;
  }

  public void setOriginatingSystemItemIds(@Nullable List<RweIdRef> originatingSystemItemIds) {
    this.originatingSystemItemIds = originatingSystemItemIds;
  }

  public @Nullable List<RweIdRef> getFeederSystemItemIds() {
    return feederSystemItemIds;
  }

  public void setFeederSystemItemIds(@Nullable List<RweIdRef> feederSystemItemIds) {
    this.feederSystemItemIds = feederSystemItemIds;
  }

  public @Nullable Encapsulated getOriginalContent() {
    return originalContent;
  }

  public void setOriginalContent(@Nullable Encapsulated originalContent) {
    this.originalContent = originalContent;
  }

  public FeederAuditDetails getOriginatingSystemAudit() {
    return originatingSystemAudit;
  }

  public void setOriginatingSystemAudit(FeederAuditDetails originatingSystemAudit) {
    this.originatingSystemAudit = originatingSystemAudit;
  }

  public @Nullable FeederAuditDetails getFeederSystemAudit() {
    return feederSystemAudit;
  }

  public void setFeederSystemAudit(@Nullable FeederAuditDetails feederSystemAudit) {
    this.feederSystemAudit = feederSystemAudit;
  }

  public String bmmClassName() {
    return "Feeder_audit";
  }

  @Override
  public String toString() {
    return "Feeder_audit";
  }
}
