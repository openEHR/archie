package org.s2.rm.base.data_types.quantity;

import org.s2.util.enumerations.*;

/**
* BMM name: Ratio_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class RatioKindEnum extends EnumerationInteger {
  static String[] _itemNames = {"rational_fraction", "fraction", "ratio"};
  static Integer[] _itemValues = {};

  public RatioKindEnum() {
    super(_itemNames, _itemValues);
  }
}
