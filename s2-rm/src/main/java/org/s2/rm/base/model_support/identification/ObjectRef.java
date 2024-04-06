package org.s2.rm.base.model_support.identification;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Object_ref
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Object_ref", propOrder = {
  "namespace",
  "type"
})
public class ObjectRef {
  /**
  * BMM name: id | BMM type: Object_id
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  // This property is in at least one descendant where it probably has a different type.
  // Skip the property in the parent class (this one).
  // private ObjectId id;

  /**
  * BMM name: namespace | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "namespace")
  private @Nullable String namespace;

  /**
  * BMM name: type | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "type")
  private String type;

  public ObjectRef() {}

  public ObjectRef(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ObjectRef otherAsObjectRef = (ObjectRef) other;
    return Objects.equals(namespace, otherAsObjectRef.namespace) &&
      Objects.equals(type, otherAsObjectRef.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), namespace, type);
  }

  public @Nullable String getNamespace() {
    return namespace;
  }

  public void setNamespace(@Nullable String namespace) {
    this.namespace = namespace;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String bmmClassName() {
    return "Object_ref";
  }

  @Override
  public String toString() {
    return "Object_ref";
  }
}
