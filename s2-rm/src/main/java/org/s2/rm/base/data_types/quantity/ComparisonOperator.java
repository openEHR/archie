package org.s2.rm.base.data_types.quantity;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Comparison_operator
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Comparison_operator", propOrder = {"value"})
public class ComparisonOperator extends StringEnumerationVar<ComparisonOperatorEnum> {

  public ComparisonOperator() {
    this.value = ComparisonOperatorEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public ComparisonOperator(String value) {
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
