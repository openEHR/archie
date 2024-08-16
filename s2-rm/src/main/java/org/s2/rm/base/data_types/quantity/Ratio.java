package org.s2.rm.base.data_types.quantity;

import java.math.BigDecimal;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Ratio
* BMM ancestors: Measurable
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ratio", propOrder = {
  "magnitude",
  "numerator",
  "denominator",
  "type"
})
public class Ratio extends Measurable {
  /**
  * BMM name: magnitude | BMM type: Decimal
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private BigDecimal magnitude;

  /**
  * BMM name: numerator | BMM type: Decimal
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "numerator")
  private BigDecimal numerator;

  /**
  * BMM name: denominator | BMM type: Decimal
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "denominator")
  private BigDecimal denominator;

  /**
  * BMM name: type | BMM type: Ratio_kind
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "type")
  private RatioKind type;

  public Ratio() {}

  public Ratio(BigDecimal magnitude, BigDecimal numerator, BigDecimal denominator, RatioKind type) {
    this.magnitude = magnitude;
    this.numerator = numerator;
    this.denominator = denominator;
    this.type = type;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Ratio otherAsRatio = (Ratio) other;
    return Objects.equals(magnitude, otherAsRatio.magnitude) &&
      Objects.equals(getPrecision(), otherAsRatio.getPrecision()) &&
      Objects.equals(numerator, otherAsRatio.numerator) &&
      Objects.equals(denominator, otherAsRatio.denominator) &&
      Objects.equals(type, otherAsRatio.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude, numerator, denominator, type);
  }

  public BigDecimal getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(BigDecimal magnitude) {
    this.magnitude = magnitude;
  }

  public BigDecimal getNumerator() {
    return numerator;
  }

  public void setNumerator(BigDecimal numerator) {
    this.numerator = numerator;
  }

  public BigDecimal getDenominator() {
    return denominator;
  }

  public void setDenominator(BigDecimal denominator) {
    this.denominator = denominator;
  }

  public RatioKind getType() {
    return type;
  }

  public void setType(RatioKind type) {
    this.type = type;
  }

  @Override
  public String bmmClassName() {
    return "Ratio";
  }

  @Override
  public String toString() {
    return "Ratio";
  }
}
