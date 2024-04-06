package org.s2.rm.base.data_types.quantity;

import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.text.CodedText;
import org.s2.rm.base.data_types.text.Text;

/**
* BMM name: Measured
* BMM generic parameters: Measured<V Measurable>
* BMM ancestors: Ordered_datum
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Measured", propOrder = {
  "value",
  "accuracy",
  "valueStatus",
  "referenceRanges",
  "interpretation",
  "interpretationGuide"
})
public class Measured<V extends Measurable> extends OrderedDatum {
  /**
  * BMM name: value | BMM type: V
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "value")
  private @Nullable V value;

  /**
  * BMM name: accuracy | BMM type: Quantity
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "accuracy")
  private @Nullable Quantity accuracy;

  /**
  * BMM name: value_status | BMM type: Comparison_operator
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "value_status")
  private @Nullable ComparisonOperator valueStatus;

  /**
  * BMM name: reference_ranges | BMM type: List<Reference_range<V>>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "reference_ranges")
  private @Nullable List<ReferenceRange<V>> referenceRanges;

  /**
  * BMM name: interpretation | BMM type: Coded_text
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "interpretation")
  private @Nullable CodedText interpretation;

  /**
  * BMM name: interpretation_guide | BMM type: Text
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "interpretation_guide")
  private @Nullable Text interpretationGuide;


  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Measured<V> otherAsMeasured = (Measured<V>) other;
    return Objects.equals(value, otherAsMeasured.value) &&
      Objects.equals(accuracy, otherAsMeasured.accuracy) &&
      Objects.equals(valueStatus, otherAsMeasured.valueStatus) &&
      Objects.equals(referenceRanges, otherAsMeasured.referenceRanges) &&
      Objects.equals(interpretation, otherAsMeasured.interpretation) &&
      Objects.equals(interpretationGuide, otherAsMeasured.interpretationGuide);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), value, accuracy, valueStatus, interpretation, interpretationGuide);
    result = referenceRanges == null ? 0 : 31 * result + referenceRanges.hashCode();
    return result;
  }

  public @Nullable V getValue() {
    return value;
  }

  public void setValue(@Nullable V value) {
    this.value = value;
  }

  public @Nullable Quantity getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(@Nullable Quantity accuracy) {
    this.accuracy = accuracy;
  }

  public @Nullable ComparisonOperator getValueStatus() {
    return valueStatus;
  }

  public void setValueStatus(@Nullable ComparisonOperator valueStatus) {
    this.valueStatus = valueStatus;
  }

  public @Nullable List<ReferenceRange<V>> getReferenceRanges() {
    return referenceRanges;
  }

  public void setReferenceRanges(@Nullable List<ReferenceRange<V>> referenceRanges) {
    this.referenceRanges = referenceRanges;
  }

  public @Nullable CodedText getInterpretation() {
    return interpretation;
  }

  public void setInterpretation(@Nullable CodedText interpretation) {
    this.interpretation = interpretation;
  }

  public @Nullable Text getInterpretationGuide() {
    return interpretationGuide;
  }

  public void setInterpretationGuide(@Nullable Text interpretationGuide) {
    this.interpretationGuide = interpretationGuide;
  }

  @Override
  public String bmmClassName() {
    return "Measured";
  }

  @Override
  public String toString() {
    return "Measured";
  }
}
