package org.s2.rm.base.change_control;

import org.s2.util.enumerations.EnumerationVarString;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Attestation_reason
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attestation_reason", propOrder = {"value"})
public class AttestationReason extends EnumerationVarString {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final AttestationReasonEnum enumeration = new AttestationReasonEnum();

  public AttestationReason() {}

  // Enumeration value constructor.
  public AttestationReason(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    AttestationReason otherAsAttestationReason = (AttestationReason) other;
    return Objects.equals(value, otherAsAttestationReason.value);
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
    return "Attestation_reason";
  }

  @Override
  public String toString() {
    return "Attestation_reason";
  }
}
