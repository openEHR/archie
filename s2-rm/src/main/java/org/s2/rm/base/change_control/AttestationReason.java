package org.s2.rm.base.change_control;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Attestation_reason
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attestation_reason", propOrder = {"value"})
public class AttestationReason extends StringEnumerationVar<AttestationReasonEnum> {

  public AttestationReason() {
    this.value = AttestationReasonEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public AttestationReason(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Attestation_reason";
  }

  @Override
  public String toString() {
    return "Attestation_reason";
  }

}
