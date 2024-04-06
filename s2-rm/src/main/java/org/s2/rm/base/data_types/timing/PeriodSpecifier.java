package org.s2.rm.base.data_types.timing;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDuration;

/**
* BMM name: Period_specifier
* BMM ancestors: Time_specifier
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Period_specifier", propOrder = {
  "period"
})
public class PeriodSpecifier extends TimeSpecifier {
  /**
  * BMM name: period | BMM type: Duration
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "period")
  private RmDuration period;

  public PeriodSpecifier() {}

  public PeriodSpecifier(RmDuration period, int eventCount) {
    super(eventCount);
    this.period = period;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PeriodSpecifier otherAsPeriodSpecifier = (PeriodSpecifier) other;
    return Objects.equals(getEventCount(), otherAsPeriodSpecifier.getEventCount()) &&
      Objects.equals(period, otherAsPeriodSpecifier.period);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), period);
  }

  public RmDuration getPeriod() {
    return period;
  }

  public void setPeriod(RmDuration period) {
    this.period = period;
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
