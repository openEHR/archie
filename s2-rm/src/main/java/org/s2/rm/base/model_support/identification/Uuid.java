package org.s2.rm.base.model_support.identification;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Uuid
* BMM ancestors: Primitive_id
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Uuid")
public class Uuid extends PrimitiveId {
  public Uuid() {}

  public Uuid(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Uuid otherAsUuid = (Uuid) other;
    return Objects.equals(getValue(), otherAsUuid.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Uuid";
  }

  @Override
  public String toString() {
    return "Uuid";
  }
}
