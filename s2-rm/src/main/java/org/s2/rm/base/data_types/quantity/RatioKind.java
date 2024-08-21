package org.s2.rm.base.data_types.quantity;

import com.nedap.archie.base.RMObject;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Ratio_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ratio_kind", propOrder = {"value"})
public class RatioKind extends RMObject {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  int value;

  /**
  * Enumeration type.
  */
  static final RatioKindEnum enumeration = new RatioKindEnum();

  public RatioKind() {}

  // Enumeration value constructor.
  public RatioKind(int value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    RatioKind otherAsRatioKind = (RatioKind) other;
    return Objects.equals(value, otherAsRatioKind.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Ratio_kind";
  }

  @Override
  public String toString() {
    return "Ratio_kind";
  }
}
