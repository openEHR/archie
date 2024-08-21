package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Version_status
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Version_status", propOrder = {"value"})
public class VersionStatus extends StringEnumerationVar<VersionStatusEnum> {

  public VersionStatus() {
    this.value = VersionStatusEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public VersionStatus(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Version_status";
  }

  @Override
  public String toString() {
    return "Version_status";
  }
}
