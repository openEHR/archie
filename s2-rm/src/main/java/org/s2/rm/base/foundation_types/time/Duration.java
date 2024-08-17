package org.s2.rm.base.foundation_types.time;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* Duration type based on IS8601 representation.
* BMM name: Duration
* BMM ancestors: Temporal
* isAbstract: false | isPrimitiveType: true | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Duration")
public class Duration extends Temporal {
  public Duration() {}

  public Duration(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Duration otherAsDuration = (Duration) other;
    return Objects.equals(getValue(), otherAsDuration.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public int compareTo(Temporal other) {
    return getDurationValue().compareTo(java.time.Duration.parse(((Duration)other).getValue()));
  }

  java.time.Duration getDurationValue() { return java.time.Duration.parse(getValue()); }


  @Override
  public String bmmClassName() {
    return "Duration";
  }

  @Override
  public String toString() {
    return "Duration";
  }
}
