package org.s2.rm.base.data_types.quantity;

import com.nedap.archie.base.RMObject;
import org.s2.util.enumerations.EnumerationVarString;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Comparison_operator
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Comparison_operator", propOrder = {"value"})
public class ComparisonOperator extends EnumerationVarString {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final ComparisonOperatorEnum enumeration = new ComparisonOperatorEnum();

  public ComparisonOperator() {}

  // Enumeration value constructor.
  public ComparisonOperator(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ComparisonOperator otherAsComparisonOperator = (ComparisonOperator) other;
    return Objects.equals(value, otherAsComparisonOperator.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Comparison_operator";
  }

  @Override
  public String toString() {
    return "Comparison_operator";
  }
}
