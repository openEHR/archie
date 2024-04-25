package org.s2.rm.base.data_types.quantity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.data_types.text.Text;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Reference_range
* BMM generic parameters: Reference_range<V Measurable>
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Reference_range", propOrder = {
  "type",
  "range",
  "phenotype"
})
public class ReferenceRange<V extends Measurable> extends RMObject {
  /**
  * BMM name: type | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "type")
  private TerminologyTerm type;

  /**
  * BMM name: range | BMM type: Range<V>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "range")
  private Range<V> range;

  /**
  * BMM name: phenotype | BMM type: Text
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "phenotype")
  private @Nullable Text phenotype;

  public ReferenceRange() {}

  public ReferenceRange(TerminologyTerm type, Range<V> range) {
    this.type = type;
    this.range = range;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ReferenceRange<V> otherAsReferenceRange = (ReferenceRange<V>) other;
    return Objects.equals(type, otherAsReferenceRange.type) &&
      Objects.equals(range, otherAsReferenceRange.range) &&
      Objects.equals(phenotype, otherAsReferenceRange.phenotype);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, range, phenotype);
  }

  public TerminologyTerm getType() {
    return type;
  }

  public void setType(TerminologyTerm type) {
    this.type = type;
  }

  public Range<V> getRange() {
    return range;
  }

  public void setRange(Range<V> range) {
    this.range = range;
  }

  public @Nullable Text getPhenotype() {
    return phenotype;
  }

  public void setPhenotype(@Nullable Text phenotype) {
    this.phenotype = phenotype;
  }

  public String bmmClassName() {
    return "Reference_range";
  }

  @Override
  public String toString() {
    return "Reference_range";
  }
}
