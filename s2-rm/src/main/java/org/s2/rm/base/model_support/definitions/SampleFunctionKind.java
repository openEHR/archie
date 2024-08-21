package org.s2.rm.base.model_support.definitions;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.StringEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Sample_function_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sample_function_kind", propOrder = {"value"})
public class SampleFunctionKind extends StringEnumerationVar<SampleFunctionKindEnum> {

  public SampleFunctionKind() {
    this.value = SampleFunctionKindEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public SampleFunctionKind(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Sample_function_kind";
  }

  @Override
  public String toString() {
    return "Sample_function_kind";
  }

}
