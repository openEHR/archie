package org.s2.rm.base.data_types.quantity;

import org.s2.rm.base.model_support.definitions.SampleFunctionKindEnum;
import org.s2.util.enumerations.*;

/**
* BMM name: Ratio_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class RatioKindEnum extends EnumerationType<Integer> {
  static String[] _itemNames = {"rational_fraction", "fraction", "ratio"};
  static Integer[] _itemValues = {};

  public RatioKindEnum() {
    super(_itemNames, _itemValues);
  }

  private static RatioKindEnum INSTANCE;
  public static RatioKindEnum getInstance() {
    if (INSTANCE == null)
      INSTANCE = new RatioKindEnum();
    return INSTANCE;
  }
}
