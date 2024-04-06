package org.s2.rm.base.foundation_types.time;


import java.time.*;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* Time type based on IS8601 representation.
* BMM name: Time
* BMM ancestors: Temporal
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Time")
public class RmTime extends Temporal {
  public RmTime() {}

  public RmTime(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    RmTime otherAsRmTime = (RmTime) other;
    return Objects.equals(getValue(), otherAsRmTime.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public int compareTo(Temporal other) {
    return getTimeValue().compareTo(LocalTime.parse(other.getValue()));
  }

  LocalTime getTimeValue() { return LocalTime.parse(getValue()); }


  @Override
  public String bmmClassName() {
    return "Time";
  }

  @Override
  public String toString() {
    return "Time";
  }
}
