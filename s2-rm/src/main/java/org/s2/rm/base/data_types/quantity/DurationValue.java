package org.s2.rm.base.data_types.quantity;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDuration;

/**
* BMM name: Duration_value
* BMM ancestors: Temporal_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Duration_value", propOrder = {
  "magnitude"
})
public class DurationValue extends TemporalValue {
  /**
  * BMM name: magnitude | BMM type: Duration
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private RmDuration magnitude;

  public DurationValue() {}

  public DurationValue(RmDuration magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    DurationValue otherAsDurationValue = (DurationValue) other;
    return Objects.equals(magnitude, otherAsDurationValue.magnitude) &&
      Objects.equals(getPrecision(), otherAsDurationValue.getPrecision());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude);
  }

  public RmDuration getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(RmDuration magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public String bmmClassName() {
    return "Duration_value";
  }

  @Override
  public String toString() {
    return "Duration_value";
  }
}
