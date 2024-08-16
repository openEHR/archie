package org.s2.rm.care.ehr;

import javax.xml.bind.annotation.*;

/**
* BMM name: Access_control_settings
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Access_control_settings")
public abstract class AccessControlSettings {
  public AccessControlSettings() {}


  public String bmmClassName() {
    return "Access_control_settings";
  }

  @Override
  public String toString() {
    return "Access_control_settings";
  }
}
