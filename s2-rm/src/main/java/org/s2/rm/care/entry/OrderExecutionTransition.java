package org.s2.rm.care.entry;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Order_execution_transition
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order_execution_transition", propOrder = {"value"})
public class OrderExecutionTransition extends StringEnumerationVar<OrderExecutionTransitionEnum> {

  public OrderExecutionTransition() {
    this.value = OrderExecutionTransitionEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public OrderExecutionTransition(String value) {
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
