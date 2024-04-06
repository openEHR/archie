package org.s2.rm.base.model_support.definitions;

import java.util.*;

/**
* BMM name: Trend_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class TrendKind {
  /**
  * Enumeration value.
  */
  String value;

  /**
  * Enumeration type.
  */
  static final TrendKindEnum enumeration = new TrendKindEnum();

  // Enumeration value constructor.
  public TrendKind(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    TrendKind otherAsTrendKind = (TrendKind) other;
    return Objects.equals(value, otherAsTrendKind.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String bmmClassName() {
    return "Trend_kind";
  }

  @Override
  public String toString() {
    return "Trend_kind";
  }
}
