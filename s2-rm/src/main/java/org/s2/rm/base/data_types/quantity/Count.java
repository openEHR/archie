package org.s2.rm.base.data_types.quantity;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Count
* BMM ancestors: Measurable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Count", propOrder = {
  "magnitude"
})
public class Count extends Measurable {
  /**
  * BMM name: magnitude | BMM type: Integer
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private int magnitude;

  public Count() {}

  public Count(int magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Count otherAsCount = (Count) other;
    return Objects.equals(magnitude, otherAsCount.magnitude) &&
      Objects.equals(getPrecision(), otherAsCount.getPrecision());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude);
  }

  public int getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(int magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public String bmmClassName() {
    return "Count";
  }

  @Override
  public String toString() {
    return "Count";
  }
}
