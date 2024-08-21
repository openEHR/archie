package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.*;

/**
* BMM name: Trend_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class TrendKindEnum extends EnumerationType<String> {
  static String[] _itemNames = {"decrease", "increase", "change"};
  static String[] _itemValues = {};

  public TrendKindEnum() {
    super(_itemNames, _itemValues);
  }
  private static TrendKindEnum INSTANCE;
  public static TrendKindEnum getInstance() {
    if (INSTANCE == null)
      INSTANCE = new TrendKindEnum();
    return INSTANCE;
  }
}
