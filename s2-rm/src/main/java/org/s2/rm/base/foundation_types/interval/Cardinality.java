package org.s2.rm.base.foundation_types.interval;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Cardinality
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cardinality", propOrder = {
  "isOrdered",
  "isUnique",
  "interval"
})
public class Cardinality {
  /**
  * BMM name: is_ordered | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "is_ordered")
  private boolean isOrdered;

  /**
  * BMM name: is_unique | BMM type: Boolean
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "is_unique")
  private boolean isUnique;

  /**
  * BMM name: interval | BMM type: Multiplicity_interval
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "interval")
  private MultiplicityInterval interval;

  public Cardinality() {}

  public Cardinality(boolean isOrdered, boolean isUnique, MultiplicityInterval interval) {
    this.isOrdered = isOrdered;
    this.isUnique = isUnique;
    this.interval = interval;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Cardinality otherAsCardinality = (Cardinality) other;
    return Objects.equals(isOrdered, otherAsCardinality.isOrdered) &&
      Objects.equals(isUnique, otherAsCardinality.isUnique) &&
      Objects.equals(interval, otherAsCardinality.interval);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), isOrdered, isUnique, interval);
  }

  public boolean getIsOrdered() {
    return isOrdered;
  }

  public void setIsOrdered(boolean isOrdered) {
    this.isOrdered = isOrdered;
  }

  public boolean getIsUnique() {
    return isUnique;
  }

  public void setIsUnique(boolean isUnique) {
    this.isUnique = isUnique;
  }

  public MultiplicityInterval getInterval() {
    return interval;
  }

  public void setInterval(MultiplicityInterval interval) {
    this.interval = interval;
  }

  public String bmmClassName() {
    return "Cardinality";
  }

  @Override
  public String toString() {
    return "Cardinality";
  }
}
