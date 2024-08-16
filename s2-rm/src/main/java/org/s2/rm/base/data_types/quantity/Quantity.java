package org.s2.rm.base.data_types.quantity;

import java.math.BigDecimal;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.text.CodedText;

/**
* BMM name: Quantity
* BMM ancestors: Measurable
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quantity", propOrder = {
  "magnitude",
  "units"
})
public class Quantity extends Measurable {
  /**
  * BMM name: magnitude | BMM type: Decimal
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private BigDecimal magnitude;

  /**
  * BMM name: units | BMM type: Coded_text
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "units")
  private CodedText units;

  public Quantity() {}

  public Quantity(BigDecimal magnitude, CodedText units) {
    this.magnitude = magnitude;
    this.units = units;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Quantity otherAsQuantity = (Quantity) other;
    return Objects.equals(magnitude, otherAsQuantity.magnitude) &&
      Objects.equals(getPrecision(), otherAsQuantity.getPrecision()) &&
      Objects.equals(units, otherAsQuantity.units);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude, units);
  }

  public BigDecimal getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(BigDecimal magnitude) {
    this.magnitude = magnitude;
  }

  public CodedText getUnits() {
    return units;
  }

  public void setUnits(CodedText units) {
    this.units = units;
  }

  @Override
  public String bmmClassName() {
    return "Quantity";
  }

  @Override
  public String toString() {
    return "Quantity";
  }
}
