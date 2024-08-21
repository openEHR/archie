package org.s2.rm.care.entry;

import org.s2.rm.base.model_support.definitions.SampleFunctionKindEnum;
import org.s2.util.enumerations.*;

/**
* BMM name: Order_execution_transition
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class OrderExecutionTransitionEnum extends EnumerationType<String> {
  static String[] _itemNames = {"initiate", "start", "schedule", "plan_step", "cancel", "do", "resume", "suspend", "suspended_step", "active_step", "scheduled_step", "postponed_step", "finish", "abort", "restore", "postpone", "time_out", "notify_completed", "notify_cancelled", "notify_aborted"};
  static String[] _itemValues = {};

  public OrderExecutionTransitionEnum() {
    super(_itemNames, _itemValues);
  }

  private static OrderExecutionTransitionEnum INSTANCE;
  public static OrderExecutionTransitionEnum getInstance() {
    if (INSTANCE == null)
      INSTANCE = new OrderExecutionTransitionEnum();
    return INSTANCE;
  }
}
