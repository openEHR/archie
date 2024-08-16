package org.s2.rm.base.data_types.timing;

import javax.xml.bind.annotation.*;

/**
* BMM name: Hour_specifier
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Hour_specifier")
public abstract class HourSpecifier {
  public HourSpecifier() {}


  public String bmmClassName() {
    return "Hour_specifier";
  }

  @Override
  public String toString() {
    return "Hour_specifier";
  }
}
