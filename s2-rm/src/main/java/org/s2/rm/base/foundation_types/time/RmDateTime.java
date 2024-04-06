package org.s2.rm.base.foundation_types.time;


import java.time.*;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* Date Time type based on IS8601 representation.
* BMM name: Date_time
* BMM ancestors: Temporal
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Date_time")
public class RmDateTime extends Temporal {
  public RmDateTime() {}

  public RmDateTime(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    RmDateTime otherAsRmDateTime = (RmDateTime) other;
    return Objects.equals(getValue(), otherAsRmDateTime.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public int compareTo(Temporal other) {
    return getDateTimeValue().compareTo(LocalDateTime.parse(other.getValue()));
  }

  LocalDateTime getDateTimeValue() { return LocalDateTime.parse(getValue()); }


  @Override
  public String bmmClassName() {
    return "Date_time";
  }

  @Override
  public String toString() {
    return "Date_time";
  }
}
