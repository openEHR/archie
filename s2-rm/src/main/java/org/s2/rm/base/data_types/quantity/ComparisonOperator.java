package org.s2.rm.base.data_types.quantity;

import java.util.*;

/**
* BMM name: Comparison_operator
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class ComparisonOperator {
  /**
  * Enumeration value.
  */
  String value;

  /**
  * Enumeration type.
  */
  static final ComparisonOperatorEnum enumeration = new ComparisonOperatorEnum();

  // Enumeration value constructor.
  public ComparisonOperator(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ComparisonOperator otherAsComparisonOperator = (ComparisonOperator) other;
    return Objects.equals(value, otherAsComparisonOperator.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String bmmClassName() {
    return "Comparison_operator";
  }

  @Override
  public String toString() {
    return "Comparison_operator";
  }
}
