package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.*;

/**
* BMM name: Validity_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class ValidityKindEnum extends EnumerationString {
  static String[] _itemNames = {"mandatory", "optional", "prohibited"};
  static String[] _itemValues = {};

  public ValidityKindEnum() {
    super(_itemNames, _itemValues);
  }
}
