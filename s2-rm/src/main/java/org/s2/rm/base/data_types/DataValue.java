package org.s2.rm.base.data_types;

import com.nedap.archie.base.RMObject;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Data_value
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Data_value")
public abstract class DataValue extends RMObject {

  public String bmmClassName() {
    return "Data_value";
  }

  @Override
  public String toString() {
    return "Data_value";
  }
}
