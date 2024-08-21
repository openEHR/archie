package org.s2.rm.base.change_control;

import org.s2.util.enumerations.*;

/**
* BMM name: Version_lifecycle_state
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class VersionLifecycleStateEnum extends EnumerationTypeString {
  static String[] _itemNames = {"initial", "incomplete", "abandoned", "inactive", "deleted", "complete"};
  static String[] _itemValues = {};

  public VersionLifecycleStateEnum() {
    super(_itemNames, _itemValues);
  }
}
