package org.s2.rm.base.data_types.quantity;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmTime;

/**
* BMM name: Time_value
* BMM ancestors: Temporal_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Time_value", propOrder = {
  "magnitude"
})
public class TimeValue extends TemporalValue {
  /**
  * BMM name: magnitude | BMM type: Time
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private RmTime magnitude;

  public TimeValue() {}

  public TimeValue(RmTime magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    TimeValue otherAsTimeValue = (TimeValue) other;
    return Objects.equals(magnitude, otherAsTimeValue.magnitude) &&
      Objects.equals(getPrecision(), otherAsTimeValue.getPrecision());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude);
  }

  public RmTime getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(RmTime magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public String bmmClassName() {
    return "Time_value";
  }

  @Override
  public String toString() {
    return "Time_value";
  }
}
