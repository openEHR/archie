package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.EnumerationVarString;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Version_status
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Version_status", propOrder = {"value"})
public class VersionStatus extends EnumerationVarString {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final VersionStatusEnum enumeration = new VersionStatusEnum();

  public VersionStatus() {}

  // Enumeration value constructor.
  public VersionStatus(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    VersionStatus otherAsVersionStatus = (VersionStatus) other;
    return Objects.equals(value, otherAsVersionStatus.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
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
