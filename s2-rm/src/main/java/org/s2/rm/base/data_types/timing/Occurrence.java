package main.java.org.s2.rm.base.data_types.timing;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDate;

/**
* BMM name: Occurrence
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Occurrence", propOrder = {
  "date",
  "timeOfDay"
})
public class Occurrence {
  /**
  * BMM name: date | BMM type: Date
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "date")
  private @Nonnull RmDate date;

  /**
  * BMM name: time_of_day | BMM type: List<Hour_specifier>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "time_of_day")
  private @Nullable List<HourSpecifier> timeOfDay;

  public Occurrence() {}

  public Occurrence(@Nonnull RmDate date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Occurrence otherAsOccurrence = (Occurrence) other;
    return Objects.equals(date, otherAsOccurrence.date) &&
      Objects.equals(timeOfDay, otherAsOccurrence.timeOfDay);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), date);
    result = timeOfDay == null ? 0 : 31 * result + timeOfDay.hashCode();
    return result;
  }

  public @Nonnull RmDate getDate() {
    return date;
  }

  public void setDate(@Nonnull RmDate date) {
    this.date = date;
  }

  public @Nullable List<HourSpecifier> getTimeOfDay() {
    return timeOfDay;
  }

  public void setTimeOfDay(@Nullable List<HourSpecifier> timeOfDay) {
    this.timeOfDay = timeOfDay;
  }

  public String bmmClassName() {
    return "Occurrence";
  }

  @Override
  public String toString() {
    return "Occurrence";
  }
}
