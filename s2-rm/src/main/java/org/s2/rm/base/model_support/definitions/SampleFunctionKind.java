package org.s2.rm.base.model_support.definitions;

import com.nedap.archie.base.RMObject;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Sample_function_kind
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sample_function_kind", propOrder = {"value"})
public class SampleFunctionKind extends RMObject {
  /**
  * Enumeration value.
  */
  @XmlElement(name = "value")
  String value;

  /**
  * Enumeration type.
  */
  static final SampleFunctionKindEnum enumeration = new SampleFunctionKindEnum();

  public SampleFunctionKind() {}

  // Enumeration value constructor.
  public SampleFunctionKind(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    SampleFunctionKind otherAsSampleFunctionKind = (SampleFunctionKind) other;
    return Objects.equals(value, otherAsSampleFunctionKind.value);
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
    return "Sample_function_kind";
  }

  @Override
  public String toString() {
    return "Sample_function_kind";
  }
}
