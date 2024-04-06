package org.s2.rm.care.entry;

import java.util.*;

/**
* BMM name: Order_execution_state
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class OrderExecutionState {
  /**
  * Enumeration value.
  */
  String value;

  /**
  * Enumeration type.
  */
  static final OrderExecutionStateEnum enumeration = new OrderExecutionStateEnum();

  // Enumeration value constructor.
  public OrderExecutionState(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    OrderExecutionState otherAsOrderExecutionState = (OrderExecutionState) other;
    return Objects.equals(value, otherAsOrderExecutionState.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String bmmClassName() {
    return "Order_execution_state";
  }

  @Override
  public String toString() {
    return "Order_execution_state";
  }
}
