package org.s2.rm.base.model_support.definitions;

import java.util.*;

/**
* BMM name: Validity_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class ValidityKind {
  /**
  * Enumeration value.
  */
  String value;

  /**
  * Enumeration type.
  */
  static final ValidityKindEnum enumeration = new ValidityKindEnum();

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

  public String bmmClassName() {
    return "Validity_kind";
  }

  @Override
  public String toString() {
    return "Validity_kind";
  }
}
