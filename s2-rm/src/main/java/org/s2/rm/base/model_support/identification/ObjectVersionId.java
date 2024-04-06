package org.s2.rm.base.model_support.identification;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Object_version_id
* BMM ancestors: Object_id
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Object_version_id")
public class ObjectVersionId extends ObjectId {
  public ObjectVersionId() {}

  public ObjectVersionId(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ObjectVersionId otherAsObjectVersionId = (ObjectVersionId) other;
    return Objects.equals(getValue(), otherAsObjectVersionId.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Object_version_id";
  }

  @Override
  public String toString() {
    return "Object_version_id";
  }
}
