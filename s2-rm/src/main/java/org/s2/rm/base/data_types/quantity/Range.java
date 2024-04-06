package org.s2.rm.base.data_types.quantity;

import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;

/**
* BMM name: Range
* BMM generic parameters: Range<V Ordered_datum>
* BMM ancestors: Data_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Range", propOrder = {
  "lower",
  "upper"
})
public class Range<V extends OrderedDatum> extends DataValue {
  /**
  * BMM name: lower | BMM type: V
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "lower")
  private @Nullable V lower;

  /**
  * BMM name: upper | BMM type: V
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "upper")
  private @Nullable V upper;


  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Range<V> otherAsRange = (Range<V>) other;
    return Objects.equals(lower, otherAsRange.lower) &&
      Objects.equals(upper, otherAsRange.upper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lower, upper);
  }

  public @Nullable V getLower() {
    return lower;
  }

  public void setLower(@Nullable V lower) {
    this.lower = lower;
  }

  public @Nullable V getUpper() {
    return upper;
  }

  public void setUpper(@Nullable V upper) {
    this.upper = upper;
  }

  @Override
  public String bmmClassName() {
    return "Range";
  }

  @Override
  public String toString() {
    return "Range";
  }
}
