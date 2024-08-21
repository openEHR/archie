package org.s2.rm.care.entry;

import com.nedap.archie.base.RMObject;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Order_execution_transition
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order_execution_transition", propOrder = {"value"})
public class OrderExecutionTransition extends RMObject {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final OrderExecutionTransitionEnum enumeration = new OrderExecutionTransitionEnum();

  public OrderExecutionTransition() {}

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

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Order_execution_transition";
  }

  @Override
  public String toString() {
    return "Order_execution_transition";
  }
}
