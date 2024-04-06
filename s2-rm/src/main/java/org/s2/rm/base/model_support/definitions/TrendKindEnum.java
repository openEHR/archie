package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.*;

/**
* BMM name: Trend_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class TrendKindEnum extends EnumerationString {
  static String[] _itemNames = {"decrease", "increase", "change"};
  static String[] _itemValues = {};

  public TrendKindEnum() {
    super(_itemNames, _itemValues);
  }
}
