package org.s2.rm.base.data_types.timing;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.foundation_types.interval.Interval;
import org.s2.rm.base.foundation_types.time.RmDate;

/**
* BMM name: Occurrence_pattern
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Occurrence_pattern", propOrder = {
  "times",
  "totalCount",
  "boundingPeriod"
})
public class OccurrencePattern extends RMObject {
  /**
  * BMM name: times | BMM type: List<Time_specifier>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "times")
  private @Nullable List<TimeSpecifier> times;

  /**
  * BMM name: total_count | BMM type: Integer
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "total_count")
  private int totalCount;

  /**
  * BMM name: bounding_period | BMM type: Interval<Date>
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
    return Objects.equals(times, otherAsOccurrencePattern.times) &&
      Objects.equals(totalCount, otherAsOccurrencePattern.totalCount) &&
      Objects.equals(boundingPeriod, otherAsOccurrencePattern.boundingPeriod);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), totalCount, boundingPeriod);
    result = times == null ? 0 : 31 * result + times.hashCode();
    return result;
  }

  public @Nullable List<TimeSpecifier> getTimes() {
    return times;
  }

  public void setTimes(@Nullable List<TimeSpecifier> times) {
    this.times = times;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
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
