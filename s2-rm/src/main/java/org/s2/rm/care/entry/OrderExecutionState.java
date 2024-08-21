package org.s2.rm.care.entry;

import org.s2.util.enumerations.EnumerationVarString;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Order_execution_state
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order_execution_state", propOrder = {"value"})
public class OrderExecutionState extends EnumerationVarString {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final OrderExecutionStateEnum enumeration = new OrderExecutionStateEnum();

  public OrderExecutionState() {}

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

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Order_execution_state";
  }

  @Override
  public String toString() {
    return "Order_execution_state";
  }
}
