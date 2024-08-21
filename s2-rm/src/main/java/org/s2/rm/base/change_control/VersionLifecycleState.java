package org.s2.rm.base.change_control;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Version_lifecycle_state
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Version_lifecycle_state", propOrder = {"value"})
public class VersionLifecycleState extends StringEnumerationVar<VersionLifecycleStateEnum> {

  public VersionLifecycleState() {
    this.value = VersionLifecycleStateEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public VersionLifecycleState(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Version_lifecycle_state";
  }

  @Override
  public String toString() {
    return "Version_lifecycle_state";
  }
}
