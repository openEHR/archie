package org.s2.rm.care.ehr;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Access_control_settings
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Access_control_settings")
public abstract class AccessControlSettings {

  public String bmmClassName() {
    return "Access_control_settings";
  }

  @Override
  public String toString() {
    return "Access_control_settings";
  }
}
