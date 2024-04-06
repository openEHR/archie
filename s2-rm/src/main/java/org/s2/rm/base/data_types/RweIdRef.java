package org.s2.rm.base.data_types;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Rwe_id_ref
* BMM ancestors: Data_value
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rwe_id_ref", propOrder = {
  "id",
  "issuer",
  "assigner",
  "type"
})
public class RweIdRef extends DataValue {
  /**
  * BMM name: id | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "id")
  private String id;

  /**
  * BMM name: issuer | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "issuer")
  private @Nullable String issuer;

  /**
  * BMM name: assigner | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "assigner")
  private @Nullable String assigner;

  /**
  * BMM name: type | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "type")
  private @Nullable String type;

  public RweIdRef() {}

  public RweIdRef(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    RweIdRef otherAsRweIdRef = (RweIdRef) other;
    return Objects.equals(id, otherAsRweIdRef.id) &&
      Objects.equals(issuer, otherAsRweIdRef.issuer) &&
      Objects.equals(assigner, otherAsRweIdRef.assigner) &&
      Objects.equals(type, otherAsRweIdRef.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, issuer, assigner, type);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public @Nullable String getIssuer() {
    return issuer;
  }

  public void setIssuer(@Nullable String issuer) {
    this.issuer = issuer;
  }

  public @Nullable String getAssigner() {
    return assigner;
  }

  public void setAssigner(@Nullable String assigner) {
    this.assigner = assigner;
  }

  public @Nullable String getType() {
    return type;
  }

  public void setType(@Nullable String type) {
    this.type = type;
  }

  @Override
  public String bmmClassName() {
    return "Rwe_id_ref";
  }

  @Override
  public String toString() {
    return "Rwe_id_ref";
  }
}
