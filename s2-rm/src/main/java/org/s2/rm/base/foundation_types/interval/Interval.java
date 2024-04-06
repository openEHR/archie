package org.s2.rm.base.foundation_types.interval;

import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* Type defining an interval of any ordered type.
* BMM name: Interval
* BMM generic parameters: Interval<T Comparable>
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Interval", propOrder = {
  "lower",
  "upper",
  "lowerUnbounded",
  "upperUnbounded",
  "lowerIncluded",
  "upperIncluded"
})
public class Interval<T extends Comparable> {
  /**
  * BMM name: lower | BMM type: T
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "lower")
  private @Nullable T lower;

  /**
  * BMM name: upper | BMM type: T
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "upper")
  private @Nullable T upper;

  /**
  * BMM name: lower_unbounded | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "lower_unbounded")
  private boolean lowerUnbounded;

  /**
  * BMM name: upper_unbounded | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "upper_unbounded")
  private boolean upperUnbounded;

  /**
  * BMM name: lower_included | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "lower_included")
  private boolean lowerIncluded;

  /**
  * BMM name: upper_included | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "upper_included")
  private boolean upperIncluded;

  public Interval() {}

  public Interval(boolean lowerUnbounded, boolean upperUnbounded, boolean lowerIncluded, boolean upperIncluded) {
    this.lowerUnbounded = lowerUnbounded;
    this.upperUnbounded = upperUnbounded;
    this.lowerIncluded = lowerIncluded;
    this.upperIncluded = upperIncluded;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Interval<T> otherAsInterval = (Interval<T>) other;
    return Objects.equals(lower, otherAsInterval.lower) &&
      Objects.equals(upper, otherAsInterval.upper) &&
      Objects.equals(lowerUnbounded, otherAsInterval.lowerUnbounded) &&
      Objects.equals(upperUnbounded, otherAsInterval.upperUnbounded) &&
      Objects.equals(lowerIncluded, otherAsInterval.lowerIncluded) &&
      Objects.equals(upperIncluded, otherAsInterval.upperIncluded);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lower, upper, lowerUnbounded, upperUnbounded, lowerIncluded, upperIncluded);
  }

  public @Nullable T getLower() {
    return lower;
  }

  public void setLower(@Nullable T lower) {
    this.lower = lower;
  }

  public @Nullable T getUpper() {
    return upper;
  }

  public void setUpper(@Nullable T upper) {
    this.upper = upper;
  }

  public boolean getLowerUnbounded() {
    return lowerUnbounded;
  }

  public void setLowerUnbounded(boolean lowerUnbounded) {
    this.lowerUnbounded = lowerUnbounded;
  }

  public boolean getUpperUnbounded() {
    return upperUnbounded;
  }

  public void setUpperUnbounded(boolean upperUnbounded) {
    this.upperUnbounded = upperUnbounded;
  }

  public boolean getLowerIncluded() {
    return lowerIncluded;
  }

  public void setLowerIncluded(boolean lowerIncluded) {
    this.lowerIncluded = lowerIncluded;
  }

  public boolean getUpperIncluded() {
    return upperIncluded;
  }

  public void setUpperIncluded(boolean upperIncluded) {
    this.upperIncluded = upperIncluded;
  }

  public String bmmClassName() {
    return "Interval";
  }

  @Override
  public String toString() {
    return "Interval";
  }
}
