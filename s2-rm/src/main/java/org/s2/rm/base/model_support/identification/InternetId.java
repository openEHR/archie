package org.s2.rm.base.model_support.identification;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Internet_id
* BMM ancestors: Primitive_id
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Internet_id")
public class InternetId extends PrimitiveId {
  public InternetId() {}

  public InternetId(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    InternetId otherAsInternetId = (InternetId) other;
    return Objects.equals(getValue(), otherAsInternetId.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Internet_id";
  }

  @Override
  public String toString() {
    return "Internet_id";
  }
}
