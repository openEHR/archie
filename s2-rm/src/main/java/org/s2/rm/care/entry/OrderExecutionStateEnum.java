package org.s2.rm.care.entry;

import org.s2.util.enumerations.*;

/**
* BMM name: Order_execution_state
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class OrderExecutionStateEnum extends EnumerationType<String> {
  static String[] _itemNames = {"initial", "planned", "scheduled", "postponed", "cancelled", "active", "suspended", "aborted", "completed", "expired"};
  static String[] _itemValues = {};

  public OrderExecutionStateEnum() {
    super(_itemNames, _itemValues);
  }

  private static OrderExecutionStateEnum INSTANCE;
  public static OrderExecutionStateEnum getInstance() {
    if (INSTANCE == null)
      INSTANCE = new OrderExecutionStateEnum();
    return INSTANCE;
  }
}
