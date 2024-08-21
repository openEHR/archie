package org.s2.rm.base.data_types.timing;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Day_hour_specifier
* BMM ancestors: Occurrence_times_specifier
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Day_hour_specifier", propOrder = {
  "day",
  "dayPeriod",
  "timesOfDay"
})
public class DayHourSpecifier extends OccurrenceTimesSpecifier {
  /**
  * BMM name: day | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  * valueConstraint: s2.DaySpecifier
  */
  @XmlElement(name = "day")
  private @Nullable TerminologyTerm day;

  /**
  * BMM name: day_period | BMM type: Integer
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "day_period")
  private int dayPeriod;

  /**
  * BMM name: times_of_day | BMM type: {@code List<Hour_specifier>}
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "times_of_day")
  private @Nullable List<HourSpecifier> timesOfDay;

  public DayHourSpecifier() {}


  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    DayHourSpecifier otherAsDayHourSpecifier = (DayHourSpecifier) other;
    return Objects.equals(getEventCount(), otherAsDayHourSpecifier.getEventCount()) &&
      Objects.equals(getEventCountUpper(), otherAsDayHourSpecifier.getEventCountUpper()) &&
      Objects.equals(day, otherAsDayHourSpecifier.day) &&
      Objects.equals(dayPeriod, otherAsDayHourSpecifier.dayPeriod) &&
      Objects.equals(timesOfDay, otherAsDayHourSpecifier.timesOfDay);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), day, dayPeriod);
    result = timesOfDay == null ? 0 : 31 * result + timesOfDay.hashCode();
    return result;
  }

  public @Nullable TerminologyTerm getDay() {
    return day;
  }

  public void setDay(@Nullable TerminologyTerm day) {
    this.day = day;
  }

  public int getDayPeriod() {
    return dayPeriod;
  }

  public void setDayPeriod(int dayPeriod) {
    this.dayPeriod = dayPeriod;
  }

  public @Nullable List<HourSpecifier> getTimesOfDay() {
    return timesOfDay;
  }

  public void setTimesOfDay(@Nullable List<HourSpecifier> timesOfDay) {
    this.timesOfDay = timesOfDay;
  }

  @Override
  public String bmmClassName() {
    return "Day_hour_specifier";
  }

  @Override
  public String toString() {
    return "Day_hour_specifier";
  }
}
