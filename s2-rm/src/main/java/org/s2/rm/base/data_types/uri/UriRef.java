package org.s2.rm.base.data_types.uri;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;

/**
* BMM name: Uri_ref
* BMM ancestors: Data_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Uri_ref", propOrder = {
  "value"
})
public class UriRef extends DataValue {
  /**
  * BMM name: value | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "value")
  private String value;

  public UriRef() {}

  public UriRef(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    UriRef otherAsUriRef = (UriRef) other;
    return Objects.equals(value, otherAsUriRef.value);
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

  @Override
  public String bmmClassName() {
    return "Uri_ref";
  }

  @Override
  public String toString() {
    return "Uri_ref";
  }
}
