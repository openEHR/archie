package main.java.org.s2.rm.base.data_types;

import jakarta.annotation.Nonnull;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Boolean_value
* BMM ancestors: Data_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Boolean_value", propOrder = {
  "value",
  "trueTerm",
  "falseTerm"
})
public class BooleanValue extends DataValue {
  /**
  * BMM name: value | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "value")
  private boolean value;

  /**
  * BMM name: true_term | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "true_term")
  private @Nonnull TerminologyTerm trueTerm;

  /**
  * BMM name: false_term | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "false_term")
  private @Nonnull TerminologyTerm falseTerm;

  public BooleanValue() {}

  public BooleanValue(boolean value, @Nonnull TerminologyTerm trueTerm, @Nonnull TerminologyTerm falseTerm) {
    this.value = value;
    this.trueTerm = trueTerm;
    this.falseTerm = falseTerm;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    BooleanValue otherAsBooleanValue = (BooleanValue) other;
    return Objects.equals(value, otherAsBooleanValue.value) &&
      Objects.equals(trueTerm, otherAsBooleanValue.trueTerm) &&
      Objects.equals(falseTerm, otherAsBooleanValue.falseTerm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value, trueTerm, falseTerm);
  }

  public boolean getValue() {
    return value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }

  public @Nonnull TerminologyTerm getTrueTerm() {
    return trueTerm;
  }

  public void setTrueTerm(@Nonnull TerminologyTerm trueTerm) {
    this.trueTerm = trueTerm;
  }

  public @Nonnull TerminologyTerm getFalseTerm() {
    return falseTerm;
  }

  public void setFalseTerm(@Nonnull TerminologyTerm falseTerm) {
    this.falseTerm = falseTerm;
  }

  @Override
  public String bmmClassName() {
    return "Boolean_value";
  }

  @Override
  public String toString() {
    return "Boolean_value";
  }
}
