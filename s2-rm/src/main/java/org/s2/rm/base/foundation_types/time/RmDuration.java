package org.s2.rm.base.foundation_types.time;


import java.time.*;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* Duration type based on IS8601 representation.
* BMM name: Duration
* BMM ancestors: Temporal
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Duration")
public class RmDuration extends Temporal {
  public RmDuration() {}

  public RmDuration(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    RmDuration otherAsRmDuration = (RmDuration) other;
    return Objects.equals(getValue(), otherAsRmDuration.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public int compareTo(Temporal other) {
    return getDurationValue().compareTo(Duration.parse(other.getValue()));
  }

  Duration getDurationValue() { return Duration.parse(getValue()); }


  @Override
  public String bmmClassName() {
    return "Duration";
  }

  @Override
  public String toString() {
    return "Duration";
  }
}
