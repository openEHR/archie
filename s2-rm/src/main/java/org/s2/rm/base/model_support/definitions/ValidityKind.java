package org.s2.rm.base.model_support.definitions;

import com.nedap.archie.base.RMObject;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Validity_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Validity_kind", propOrder = {"value"})
public class ValidityKind extends RMObject {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final ValidityKindEnum enumeration = new ValidityKindEnum();

  public ValidityKind() {}

  // Enumeration value constructor.
  public ValidityKind(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ValidityKind otherAsValidityKind = (ValidityKind) other;
    return Objects.equals(value, otherAsValidityKind.value);
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
    return "Validity_kind";
  }

  @Override
  public String toString() {
    return "Validity_kind";
  }
}
