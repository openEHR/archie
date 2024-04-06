package org.s2.rm.base.data_types.quantity;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.text.CodedText;

/**
* BMM name: Coded_ordinal
* BMM ancestors: Ordered_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Coded_ordinal", propOrder = {
  "magnitude",
  "concept"
})
public class CodedOrdinal extends OrderedValue {
  /**
  * BMM name: magnitude | BMM type: Real
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "magnitude")
  private double magnitude;

  /**
  * BMM name: concept | BMM type: Coded_text
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "concept")
  private CodedText concept;

  public CodedOrdinal() {}

  public CodedOrdinal(double magnitude, CodedText concept) {
    this.magnitude = magnitude;
    this.concept = concept;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    CodedOrdinal otherAsCodedOrdinal = (CodedOrdinal) other;
    return Objects.equals(magnitude, otherAsCodedOrdinal.magnitude) &&
      Objects.equals(getPrecision(), otherAsCodedOrdinal.getPrecision()) &&
      Objects.equals(concept, otherAsCodedOrdinal.concept);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), magnitude, concept);
  }

  public double getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(double magnitude) {
    this.magnitude = magnitude;
  }

  public CodedText getConcept() {
    return concept;
  }

  public void setConcept(CodedText concept) {
    this.concept = concept;
  }

  @Override
  public String bmmClassName() {
    return "Coded_ordinal";
  }

  @Override
  public String toString() {
    return "Coded_ordinal";
  }
}
