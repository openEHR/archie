package org.s2.rm.care.entry;

import java.util.*;

/**
* BMM name: Order_execution_transition
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class OrderExecutionTransition {
  /**
  * Enumeration value.
  */
  String value;

  /**
  * Enumeration type.
  */
  static final OrderExecutionTransitionEnum enumeration = new OrderExecutionTransitionEnum();

  // Enumeration value constructor.
  public OrderExecutionTransition(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    OrderExecutionTransition otherAsOrderExecutionTransition = (OrderExecutionTransition) other;
    return Objects.equals(value, otherAsOrderExecutionTransition.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String bmmClassName() {
    return "Order_execution_transition";
  }

  @Override
  public String toString() {
    return "Order_execution_transition";
  }
}
