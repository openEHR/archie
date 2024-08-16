package org.s2.rm.base.change_control;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Audit_change_type
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Audit_change_type", propOrder = {"value"})
public class AuditChangeType {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final AuditChangeTypeEnum enumeration = new AuditChangeTypeEnum();

  public AuditChangeType() {}

  // Enumeration value constructor.
  public AuditChangeType(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    AuditChangeType otherAsAuditChangeType = (AuditChangeType) other;
    return Objects.equals(value, otherAsAuditChangeType.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Audit_change_type";
  }

  @Override
  public String toString() {
    return "Audit_change_type";
  }
}
