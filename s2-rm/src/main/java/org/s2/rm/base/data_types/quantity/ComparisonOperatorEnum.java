package org.s2.rm.base.data_types.quantity;

import org.s2.util.enumerations.*;

/**
* BMM name: Comparison_operator
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class ComparisonOperatorEnum extends EnumerationString {
  static String[] _itemNames = {"<", "<=", ">", ">=", "="};
  static String[] _itemValues = {};

  public ComparisonOperatorEnum() {
    super(_itemNames, _itemValues);
  }
}
