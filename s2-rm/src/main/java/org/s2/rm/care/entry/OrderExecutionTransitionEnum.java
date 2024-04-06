package org.s2.rm.care.entry;

import org.s2.util.enumerations.*;

/**
* BMM name: Order_execution_transition
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class OrderExecutionTransitionEnum extends EnumerationString {
  static String[] _itemNames = {"initiate", "start", "schedule", "plan_step", "cancel", "do", "resume", "suspend", "suspended_step", "active_step", "scheduled_step", "postponed_step", "finish", "abort", "restore", "postpone", "time_out", "notify_completed", "notify_cancelled", "notify_aborted"};
  static String[] _itemValues = {};

  public OrderExecutionTransitionEnum() {
    super(_itemNames, _itemValues);
  }
}
