package org.s2.rm.base.data_types.timing;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Day_specifier
* BMM ancestors: Time_specifier
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Day_specifier", propOrder = {
  "day",
  "timeOfDay"
})
public class DaySpecifier extends TimeSpecifier {
  /**
  * BMM name: day | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "day")
  private @Nullable TerminologyTerm day;

  /**
  * BMM name: time_of_day | BMM type: List<Hour_specifier>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "time_of_day")
  private @Nullable List<HourSpecifier> timeOfDay;

  public DaySpecifier() {}

  public DaySpecifier(int eventCount) {
    super(eventCount);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    DaySpecifier otherAsDaySpecifier = (DaySpecifier) other;
    return Objects.equals(getEventCount(), otherAsDaySpecifier.getEventCount()) &&
      Objects.equals(day, otherAsDaySpecifier.day) &&
      Objects.equals(timeOfDay, otherAsDaySpecifier.timeOfDay);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), day);
    result = timeOfDay == null ? 0 : 31 * result + timeOfDay.hashCode();
    return result;
  }

  public @Nullable TerminologyTerm getDay() {
    return day;
  }

  public void setDay(@Nullable TerminologyTerm day) {
    this.day = day;
  }

  public @Nullable List<HourSpecifier> getTimeOfDay() {
    return timeOfDay;
  }

  public void setTimeOfDay(@Nullable List<HourSpecifier> timeOfDay) {
    this.timeOfDay = timeOfDay;
  }

  @Override
  public String bmmClassName() {
    return "Day_specifier";
  }

  @Override
  public String toString() {
    return "Day_specifier";
  }
}
