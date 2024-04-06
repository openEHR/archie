package org.s2.rm.base.model_support.definitions;

import java.util.*;

/**
* BMM name: Version_status
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
public class VersionStatus {
  /**
  * Enumeration value.
  */
  String value;

  /**
  * Enumeration type.
  */
  static final VersionStatusEnum enumeration = new VersionStatusEnum();

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

  public String bmmClassName() {
    return "Version_status";
  }

  @Override
  public String toString() {
    return "Version_status";
  }
}
