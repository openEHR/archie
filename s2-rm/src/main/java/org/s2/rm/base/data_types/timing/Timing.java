package org.s2.rm.base.data_types.timing;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;
import org.s2.rm.base.foundation_types.time.Duration;

/**
* BMM name: Timing
* BMM ancestors: Data_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Timing", propOrder = {
  "history",
  "pattern",
  "eventDuration",
  "eventDurationUpper"
})
public class Timing extends DataValue {
  /**
  * BMM name: history | BMM type: List<{@literal Occurrence}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "history")
  private @Nullable List<Occurrence> history;

  /**
  * BMM name: pattern | BMM type: Occurrence_pattern
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "pattern")
  private @Nullable OccurrencePattern pattern;

  /**
  * BMM name: event_duration | BMM type: Duration
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "event_duration")
  private @Nullable Duration eventDuration;

  /**
  * BMM name: event_duration_upper | BMM type: Duration
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "event_duration_upper")
  private @Nullable Duration eventDurationUpper;

  public Timing() {}


  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Timing otherAsTiming = (Timing) other;
    return Objects.equals(history, otherAsTiming.history) &&
      Objects.equals(pattern, otherAsTiming.pattern) &&
      Objects.equals(eventDuration, otherAsTiming.eventDuration) &&
      Objects.equals(eventDurationUpper, otherAsTiming.eventDurationUpper);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), pattern, eventDuration, eventDurationUpper);
    result = history == null ? 0 : 31 * result + history.hashCode();
    return result;
  }

  public @Nullable List<Occurrence> getHistory() {
    return history;
  }

  public void setHistory(@Nullable List<Occurrence> history) {
    this.history = history;
  }

  public @Nullable OccurrencePattern getPattern() {
    return pattern;
  }

  public void setPattern(@Nullable OccurrencePattern pattern) {
    this.pattern = pattern;
  }

  public @Nullable Duration getEventDuration() {
    return eventDuration;
  }

  public void setEventDuration(@Nullable Duration eventDuration) {
    this.eventDuration = eventDuration;
  }

  public @Nullable Duration getEventDurationUpper() {
    return eventDurationUpper;
  }

  public void setEventDurationUpper(@Nullable Duration eventDurationUpper) {
    this.eventDurationUpper = eventDurationUpper;
  }

  @Override
  public String bmmClassName() {
    return "Timing";
  }

  @Override
  public String toString() {
    return "Timing";
  }
}
