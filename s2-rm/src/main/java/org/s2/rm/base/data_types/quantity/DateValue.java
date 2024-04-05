package main.java.org.s2.rm.base.data_types.quantity;

import jakarta.annotation.Nonnull;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDate;

/**
* BMM name: Date_value
* BMM ancestors: Temporal_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Date_value", propOrder = {
  "magnitude"
})
public class DateValue extends TemporalValue {
  /**
  * BMM name: magnitude | BMM type: Date
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private @Nonnull RmDate magnitude;

  public DateValue() {}

  public DateValue(@Nonnull RmDate magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    DateValue otherAsDateValue = (DateValue) other;
    return Objects.equals(magnitude, otherAsDateValue.magnitude) &&
      Objects.equals(getPrecision(), otherAsDateValue.getPrecision());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude);
  }

  public @Nonnull RmDate getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(@Nonnull RmDate magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public String bmmClassName() {
    return "Date_value";
  }

  @Override
  public String toString() {
    return "Date_value";
  }
}
