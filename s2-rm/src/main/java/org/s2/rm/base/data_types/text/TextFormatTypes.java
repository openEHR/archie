package org.s2.rm.base.data_types.text;

import com.nedap.archie.base.RMObject;

import java.util.*;

/**
* BMM name: Text_format_types
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class TextFormatTypes extends RMObject {
  /**
  * Enumeration value.
  */
  int value;

  /**
  * Enumeration type.
  */
  static final TextFormatTypesEnum enumeration = new TextFormatTypesEnum();

  // Enumeration value constructor.
  public TextFormatTypes(int value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    TextFormatTypes otherAsTextFormatTypes = (TextFormatTypes) other;
    return Objects.equals(value, otherAsTextFormatTypes.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String bmmClassName() {
    return "Text_format_types";
  }

  @Override
  public String toString() {
    return "Text_format_types";
  }
}
