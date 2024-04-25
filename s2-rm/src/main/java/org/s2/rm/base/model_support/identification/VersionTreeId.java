package org.s2.rm.base.model_support.identification;


import com.nedap.archie.base.RMObject;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Version_tree_id
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Version_tree_id", propOrder = {
  "value"
})
public class VersionTreeId extends RMObject {
  /**
  * BMM name: value | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "value")
  private String value;

  public VersionTreeId() {}

  public VersionTreeId(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    VersionTreeId otherAsVersionTreeId = (VersionTreeId) other;
    return Objects.equals(value, otherAsVersionTreeId.value);
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
    return "Version_tree_id";
  }

  @Override
  public String toString() {
    return "Version_tree_id";
  }
}
