package main.java.org.s2.rm.base.data_types.quantity;

import jakarta.annotation.Nonnull;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Proportion
* BMM ancestors: Measurable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Proportion", propOrder = {
  "magnitude",
  "numerator",
  "denominator"
})
public class Proportion extends Measurable {
  /**
  * BMM name: magnitude | BMM type: Real
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private double magnitude;

  /**
  * BMM name: numerator | BMM type: Quantity
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "numerator")
  private @Nonnull Quantity numerator;

  /**
  * BMM name: denominator | BMM type: Quantity
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "denominator")
  private @Nonnull Quantity denominator;

  public Proportion() {}

  public Proportion(double magnitude, @Nonnull Quantity numerator, @Nonnull Quantity denominator) {
    this.magnitude = magnitude;
    this.numerator = numerator;
    this.denominator = denominator;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Proportion otherAsProportion = (Proportion) other;
    return Objects.equals(magnitude, otherAsProportion.magnitude) &&
      Objects.equals(getPrecision(), otherAsProportion.getPrecision()) &&
      Objects.equals(numerator, otherAsProportion.numerator) &&
      Objects.equals(denominator, otherAsProportion.denominator);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude, numerator, denominator);
  }

  public double getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(double magnitude) {
    this.magnitude = magnitude;
  }

  public @Nonnull Quantity getNumerator() {
    return numerator;
  }

  public void setNumerator(@Nonnull Quantity numerator) {
    this.numerator = numerator;
  }

  public @Nonnull Quantity getDenominator() {
    return denominator;
  }

  public void setDenominator(@Nonnull Quantity denominator) {
    this.denominator = denominator;
  }

  @Override
  public String bmmClassName() {
    return "Proportion";
  }

  @Override
  public String toString() {
    return "Proportion";
  }
}
