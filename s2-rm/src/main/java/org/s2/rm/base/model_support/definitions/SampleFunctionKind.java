package org.s2.rm.base.model_support.definitions;

import java.util.*;

/**
* BMM name: Sample_function_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class SampleFunctionKind {
  /**
  * Enumeration value.
  */
  String value;

  /**
  * Enumeration type.
  */
  static final SampleFunctionKindEnum enumeration = new SampleFunctionKindEnum();

  // Enumeration value constructor.
  public SampleFunctionKind(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    SampleFunctionKind otherAsSampleFunctionKind = (SampleFunctionKind) other;
    return Objects.equals(value, otherAsSampleFunctionKind.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String bmmClassName() {
    return "Sample_function_kind";
  }

  @Override
  public String toString() {
    return "Sample_function_kind";
  }
}
