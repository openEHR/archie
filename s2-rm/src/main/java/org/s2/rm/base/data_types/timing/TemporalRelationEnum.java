package org.s2.rm.base.data_types.timing;

import org.s2.rm.base.data_types.quantity.RatioKindEnum;
import org.s2.util.enumerations.*;

/**
* BMM name: Temporal_relation
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class TemporalRelationEnum extends EnumerationType<Integer> {
  static String[] _itemNames = {"after", "before", "with"};
  static Integer[] _itemValues = {};

  public TemporalRelationEnum() {
    super(_itemNames, _itemValues);
  }

  private static TemporalRelationEnum INSTANCE;
  public static TemporalRelationEnum getInstance() {
    if (INSTANCE == null)
      INSTANCE = new TemporalRelationEnum();
    return INSTANCE;
  }
}
