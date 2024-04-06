package org.s2.rm.base.foundation_types.primitive_types;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Uri
* BMM ancestors: String
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Uri")
public class Uri {
  // Value of the primitive type.
  private String value;

  public Uri() {}

  public Uri(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Uri otherAsUri = (Uri) other;
    return Objects.equals(value, otherAsUri.value);
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
    return "Uri";
  }

  @Override
  public String toString() {
    return value;
  }
}
