package org.s2.rm.base.data_types.timing;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDuration;
import org.s2.rm.base.foundation_types.time.RmTime;

/**
* BMM name: Clock_time
* BMM ancestors: Hour_specifier
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Clock_time", propOrder = {
  "time"
})
public class ClockTime extends HourSpecifier {
  /**
  * BMM name: time | BMM type: Time
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "time")
  private RmTime time;

  public ClockTime() {}

  public ClockTime(RmTime time) {
    this.time = time;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ClockTime otherAsClockTime = (ClockTime) other;
    return Objects.equals(getDuration(), otherAsClockTime.getDuration()) &&
      Objects.equals(time, otherAsClockTime.time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), time);
  }

  public RmTime getTime() {
    return time;
  }

  public void setTime(RmTime time) {
    this.time = time;
  }

  @Override
  public String bmmClassName() {
    return "Clock_time";
  }

  @Override
  public String toString() {
    return "Clock_time";
  }
}
