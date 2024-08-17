package org.s2.rm.base.data_types.timing;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.Duration;

/**
* BMM name: Period_specifier
* BMM ancestors: Occurrence_times_specifier
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Period_specifier", propOrder = {
  "period",
  "periodUpper"
})
public class PeriodSpecifier extends OccurrenceTimesSpecifier {
  /**
  * BMM name: period | BMM type: Duration
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "period")
  private Duration period;

  /**
  * BMM name: period_upper | BMM type: Duration
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "period_upper")
  private @Nullable Duration periodUpper;

  public PeriodSpecifier() {}

  public PeriodSpecifier(Duration period) {
    this.period = period;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PeriodSpecifier otherAsPeriodSpecifier = (PeriodSpecifier) other;
    return Objects.equals(getEventCount(), otherAsPeriodSpecifier.getEventCount()) &&
      Objects.equals(getEventCountUpper(), otherAsPeriodSpecifier.getEventCountUpper()) &&
      Objects.equals(period, otherAsPeriodSpecifier.period) &&
      Objects.equals(periodUpper, otherAsPeriodSpecifier.periodUpper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), period, periodUpper);
  }

  public Duration getPeriod() {
    return period;
  }

  public void setPeriod(Duration period) {
    this.period = period;
  }

  public @Nullable Duration getPeriodUpper() {
    return periodUpper;
  }

  public void setPeriodUpper(@Nullable Duration periodUpper) {
    this.periodUpper = periodUpper;
  }

  @Override
  public String bmmClassName() {
    return "Period_specifier";
  }

  @Override
  public String toString() {
    return "Period_specifier";
  }
}
