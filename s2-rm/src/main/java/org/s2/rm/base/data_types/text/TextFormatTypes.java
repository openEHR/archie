package org.s2.rm.base.data_types.text;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.IntegerEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Text_format_types
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Text_format_types", propOrder = {"value"})
public class TextFormatTypes extends IntegerEnumerationVar<TextFormatTypesEnum> {

  public TextFormatTypes() {
    this.value = TextFormatTypesEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public TextFormatTypes(int value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Text_format_types";
  }

  @Override
  public String toString() {
    return "Text_format_types";
  }
}
