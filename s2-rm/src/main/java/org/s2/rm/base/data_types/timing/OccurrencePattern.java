package org.s2.rm.base.data_types.timing;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.interval.Interval;
import org.s2.rm.base.foundation_types.time.RmDate;

/**
* BMM name: Occurrence_pattern
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Occurrence_pattern", propOrder = {
  "occurrenceTimes",
  "totalEventCount",
  "boundingPeriod"
})
public class OccurrencePattern {
  /**
  * BMM name: occurrence_times | BMM type: List<{@literal Occurrence_times_specifier}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "occurrence_times")
  private @Nullable List<OccurrenceTimesSpecifier> occurrenceTimes;

  /**
  * BMM name: total_event_count | BMM type: Integer
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "total_event_count")
  private int totalEventCount;

  /**
  * BMM name: bounding_period | BMM type: Interval<{@literal Date}>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "bounding_period")
  private Interval<RmDate> boundingPeriod;

  public OccurrencePattern() {}

  public OccurrencePattern(Interval<RmDate> boundingPeriod) {
    this.boundingPeriod = boundingPeriod;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    OccurrencePattern otherAsOccurrencePattern = (OccurrencePattern) other;
    return Objects.equals(occurrenceTimes, otherAsOccurrencePattern.occurrenceTimes) &&
      Objects.equals(totalEventCount, otherAsOccurrencePattern.totalEventCount) &&
      Objects.equals(boundingPeriod, otherAsOccurrencePattern.boundingPeriod);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), totalEventCount, boundingPeriod);
    result = occurrenceTimes == null ? 0 : 31 * result + occurrenceTimes.hashCode();
    return result;
  }

  public @Nullable List<OccurrenceTimesSpecifier> getOccurrenceTimes() {
    return occurrenceTimes;
  }

  public void setOccurrenceTimes(@Nullable List<OccurrenceTimesSpecifier> occurrenceTimes) {
    this.occurrenceTimes = occurrenceTimes;
  }

  public int getTotalEventCount() {
    return totalEventCount;
  }

  public void setTotalEventCount(int totalEventCount) {
    this.totalEventCount = totalEventCount;
  }

  public Interval<RmDate> getBoundingPeriod() {
    return boundingPeriod;
  }

  public void setBoundingPeriod(Interval<RmDate> boundingPeriod) {
    this.boundingPeriod = boundingPeriod;
  }

  public String bmmClassName() {
    return "Occurrence_pattern";
  }

  @Override
  public String toString() {
    return "Occurrence_pattern";
  }
}
