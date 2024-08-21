package org.s2.rm.base.change_control;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Audit_change_type
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Audit_change_type", propOrder = {"value"})
public class AuditChangeType extends StringEnumerationVar<AuditChangeTypeEnum> {

  public AuditChangeType() {
    this.value = AuditChangeTypeEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public AuditChangeType(String value) {
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
