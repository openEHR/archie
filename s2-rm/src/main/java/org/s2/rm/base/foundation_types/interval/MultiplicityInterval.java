package org.s2.rm.base.foundation_types.interval;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Multiplicity_interval
* BMM ancestors: Interval<Integer>
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Multiplicity_interval")
public class MultiplicityInterval<T extends Integer> extends Interval<Integer> {
  public MultiplicityInterval() {}

  public MultiplicityInterval(boolean lowerUnbounded, boolean upperUnbounded, boolean lowerIncluded, boolean upperIncluded) {
    super(lowerUnbounded, upperUnbounded, lowerIncluded, upperIncluded);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    MultiplicityInterval<T> otherAsMultiplicityInterval = (MultiplicityInterval<T>) other;
    return Objects.equals(getLower(), otherAsMultiplicityInterval.getLower()) &&
      Objects.equals(getUpper(), otherAsMultiplicityInterval.getUpper()) &&
      Objects.equals(getLowerUnbounded(), otherAsMultiplicityInterval.getLowerUnbounded()) &&
      Objects.equals(getUpperUnbounded(), otherAsMultiplicityInterval.getUpperUnbounded()) &&
      Objects.equals(getLowerIncluded(), otherAsMultiplicityInterval.getLowerIncluded()) &&
      Objects.equals(getUpperIncluded(), otherAsMultiplicityInterval.getUpperIncluded());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Multiplicity_interval";
  }

  @Override
  public String toString() {
    return "Multiplicity_interval";
  }
}
