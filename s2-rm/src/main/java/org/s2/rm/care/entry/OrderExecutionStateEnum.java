package org.s2.rm.care.entry;

import org.s2.util.enumerations.*;

/**
* BMM name: Order_execution_state
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class OrderExecutionStateEnum extends EnumerationString {
  static String[] _itemNames = {"initial", "planned", "scheduled", "postponed", "cancelled", "active", "suspended", "aborted", "completed", "expired"};
  static String[] _itemValues = {};

  public OrderExecutionStateEnum() {
    super(_itemNames, _itemValues);
  }
}
