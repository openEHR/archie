package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.*;

/**
* BMM name: Sample_function_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class SampleFunctionKindEnum extends EnumerationTypeString {
  static String[] _itemNames = {"actual", "minimum", "maximum", "mean", "mode", "median", "increase", "decrease", "change", "total"};
  static String[] _itemValues = {};

  public SampleFunctionKindEnum() {
    super(_itemNames, _itemValues);
  }
}
