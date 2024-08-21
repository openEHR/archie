package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Validity_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Validity_kind", propOrder = {"value"})
public class ValidityKind extends StringEnumerationVar<ValidityKindEnum> {

  public ValidityKind() {
    this.value = ValidityKindEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public ValidityKind(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Validity_kind";
  }

  @Override
  public String toString() {
    return "Validity_kind";
  }
}
