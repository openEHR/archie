package org.s2.rm.base.data_types.text;

import org.s2.rm.base.model_support.definitions.SampleFunctionKindEnum;
import org.s2.util.enumerations.*;

/**
* BMM name: Text_format_types
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class TextFormatTypesEnum extends EnumerationType<Integer> {
  static String[] _itemNames = {"plain", "plain_no_newlines", "markdown"};
  static Integer[] _itemValues = {};

  public TextFormatTypesEnum() {
    super(_itemNames, _itemValues);
  }

  private static TextFormatTypesEnum INSTANCE;
  public static TextFormatTypesEnum getInstance() {
    if (INSTANCE == null)
      INSTANCE = new TextFormatTypesEnum();
    return INSTANCE;
  }}
