package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.*;

/**
* BMM name: Sample_function_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class SampleFunctionKindEnum extends EnumerationString {
  static String[] _itemNames = {"actual", "minimum", "maximum", "mean", "mode", "median", "increase", "decrease", "change", "total"};
  static String[] _itemValues = {};

  public SampleFunctionKindEnum() {
    super(_itemNames, _itemValues);
  }
}
