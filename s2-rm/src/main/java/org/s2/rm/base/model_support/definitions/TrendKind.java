package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Trend_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Trend_kind", propOrder = {"value"})
public class TrendKind extends StringEnumerationVar<TrendKindEnum> {

  public TrendKind() {
    this.value = TrendKindEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public TrendKind(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Trend_kind";
  }

  @Override
  public String toString() {
    return "Trend_kind";
  }
}
