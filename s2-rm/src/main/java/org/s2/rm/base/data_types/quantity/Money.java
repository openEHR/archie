package org.s2.rm.base.data_types.quantity;

import java.math.BigDecimal;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Money
* BMM ancestors: Ordered_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Money", propOrder = {
  "magnitude",
  "currency"
})
public class Money extends OrderedValue {
  /**
  * BMM name: magnitude | BMM type: Decimal
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private BigDecimal magnitude;

  /**
  * BMM name: currency | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "currency")
  private TerminologyCode currency;

  public Money() {}

  public Money(BigDecimal magnitude, TerminologyCode currency) {
    this.magnitude = magnitude;
    this.currency = currency;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Money otherAsMoney = (Money) other;
    return Objects.equals(magnitude, otherAsMoney.magnitude) &&
      Objects.equals(getPrecision(), otherAsMoney.getPrecision()) &&
      Objects.equals(currency, otherAsMoney.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude, currency);
  }

  public BigDecimal getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(BigDecimal magnitude) {
    this.magnitude = magnitude;
  }

  public TerminologyCode getCurrency() {
    return currency;
  }

  public void setCurrency(TerminologyCode currency) {
    this.currency = currency;
  }

  @Override
  public String bmmClassName() {
    return "Money";
  }

  @Override
  public String toString() {
    return "Money";
  }
}