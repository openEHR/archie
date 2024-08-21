package org.s2.rm.base.change_control;

import com.nedap.archie.base.RMObject;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Version_lifecycle_state
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Version_lifecycle_state", propOrder = {"value"})
public class VersionLifecycleState extends RMObject {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final VersionLifecycleStateEnum enumeration = new VersionLifecycleStateEnum();

  public VersionLifecycleState() {}

  // Enumeration value constructor.
  public VersionLifecycleState(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    VersionLifecycleState otherAsVersionLifecycleState = (VersionLifecycleState) other;
    return Objects.equals(value, otherAsVersionLifecycleState.value);
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
    return "Version_lifecycle_state";
  }

  @Override
  public String toString() {
    return "Version_lifecycle_state";
  }
}
