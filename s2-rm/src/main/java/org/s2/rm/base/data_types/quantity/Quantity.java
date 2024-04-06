package org.s2.rm.base.data_types.quantity;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Quantity
* BMM ancestors: Measurable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quantity", propOrder = {
  "magnitude",
  "units"
})
public class Quantity extends Measurable {
  /**
  * BMM name: magnitude | BMM type: Real
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private double magnitude;

  /**
  * BMM name: units | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "units")
  private TerminologyTerm units;

  public Quantity() {}

  public Quantity(double magnitude, TerminologyTerm units) {
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

  public double getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(double magnitude) {
    this.magnitude = magnitude;
  }

  public TerminologyTerm getUnits() {
    return units;
  }

  public void setUnits(TerminologyTerm units) {
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
