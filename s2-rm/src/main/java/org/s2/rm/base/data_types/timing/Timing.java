package org.s2.rm.base.data_types.timing;

import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;

/**
* BMM name: Timing
* BMM ancestors: Data_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Timing", propOrder = {
  "history",
  "pattern"
})
public class Timing extends DataValue {
  /**
  * BMM name: history | BMM type: List<Occurrence>
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


  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Timing otherAsTiming = (Timing) other;
    return Objects.equals(history, otherAsTiming.history) &&
      Objects.equals(pattern, otherAsTiming.pattern);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), pattern);
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

  @Override
  public String bmmClassName() {
    return "Timing";
  }

  @Override
  public String toString() {
    return "Timing";
  }
}
