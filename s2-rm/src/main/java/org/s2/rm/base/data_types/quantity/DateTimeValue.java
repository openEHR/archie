package org.s2.rm.base.data_types.quantity;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDateTime;

/**
* BMM name: Date_time_value
* BMM ancestors: Temporal_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Date_time_value", propOrder = {
  "magnitude"
})
public class DateTimeValue extends TemporalValue {
  /**
  * BMM name: magnitude | BMM type: Date_time
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private RmDateTime magnitude;

  public DateTimeValue() {}

  public DateTimeValue(RmDateTime magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    DateTimeValue otherAsDateTimeValue = (DateTimeValue) other;
    return Objects.equals(magnitude, otherAsDateTimeValue.magnitude) &&
      Objects.equals(getPrecision(), otherAsDateTimeValue.getPrecision());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude);
  }

  public RmDateTime getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(RmDateTime magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public String bmmClassName() {
    return "Date_time_value";
  }

  @Override
  public String toString() {
    return "Date_time_value";
  }
}
