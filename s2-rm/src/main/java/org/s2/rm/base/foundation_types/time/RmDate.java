package org.s2.rm.base.foundation_types.time;


import java.time.*;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* Date type based on IS8601 representation.
* BMM name: Date
* BMM ancestors: Temporal
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Date")
public class RmDate extends Temporal {
  public RmDate() {}

  public RmDate(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    RmDate otherAsRmDate = (RmDate) other;
    return Objects.equals(getValue(), otherAsRmDate.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public int compareTo(Temporal other) {
    return getDateValue().compareTo(LocalDate.parse(other.getValue()));
  }

  LocalDate getDateValue() { return LocalDate.parse(getValue()); }


  @Override
  public String bmmClassName() {
    return "Date";
  }

  @Override
  public String toString() {
    return "Date";
  }
}
