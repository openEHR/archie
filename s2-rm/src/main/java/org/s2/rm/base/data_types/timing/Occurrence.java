package org.s2.rm.base.data_types.timing;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.foundation_types.time.RmDate;

/**
* BMM name: Occurrence
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Occurrence", propOrder = {
  "date",
  "timesOfDay"
})
public class Occurrence extends RMObject {
  /**
  * BMM name: date | BMM type: Date
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "date")
  private RmDate date;

  /**
  * BMM name: times_of_day | BMM type: List<{@literal Hour_specifier}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "times_of_day")
  private @Nullable List<HourSpecifier> timesOfDay;

  public Occurrence() {}

  public Occurrence(RmDate date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Occurrence otherAsOccurrence = (Occurrence) other;
    return Objects.equals(date, otherAsOccurrence.date) &&
      Objects.equals(timesOfDay, otherAsOccurrence.timesOfDay);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), date);
    result = timesOfDay == null ? 0 : 31 * result + timesOfDay.hashCode();
    return result;
  }

  public RmDate getDate() {
    return date;
  }

  public void setDate(RmDate date) {
    this.date = date;
  }

  public @Nullable List<HourSpecifier> getTimesOfDay() {
    return timesOfDay;
  }

  public void setTimesOfDay(@Nullable List<HourSpecifier> timesOfDay) {
    this.timesOfDay = timesOfDay;
  }

  public String bmmClassName() {
    return "Occurrence";
  }

  @Override
  public String toString() {
    return "Occurrence";
  }
}
