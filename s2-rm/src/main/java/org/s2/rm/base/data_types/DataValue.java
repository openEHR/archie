package org.s2.rm.base.data_types;

import javax.xml.bind.annotation.*;

/**
* BMM name: Data_value
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Data_value")
public abstract class DataValue {
  public DataValue() {}


  public String bmmClassName() {
    return "Data_value";
  }

  @Override
  public String toString() {
    return "Data_value";
  }
}
