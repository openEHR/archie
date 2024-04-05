package main.java.org.s2.rm.base.model_support.identification;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Locatable_ref
* BMM ancestors: Object_ref
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Locatable_ref", propOrder = {
  "id",
  "path"
})
public class LocatableRef extends ObjectRef {
  /**
  * BMM name: id | BMM type: Object_version_id
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "id")
  private @Nonnull ObjectVersionId id;

  /**
  * BMM name: path | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "path")
  private @Nullable String path;

  public LocatableRef() {}

  public LocatableRef(@Nonnull ObjectVersionId id, @Nonnull String type) {
    super(type);
    this.id = id;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    LocatableRef otherAsLocatableRef = (LocatableRef) other;
    return Objects.equals(id, otherAsLocatableRef.id) &&
      Objects.equals(getNamespace(), otherAsLocatableRef.getNamespace()) &&
      Objects.equals(getType(), otherAsLocatableRef.getType()) &&
      Objects.equals(path, otherAsLocatableRef.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, path);
  }

  public @Nonnull ObjectVersionId getId() {
    return id;
  }

  public void setId(@Nonnull ObjectVersionId id) {
    this.id = id;
  }

  public @Nullable String getPath() {
    return path;
  }

  public void setPath(@Nullable String path) {
    this.path = path;
  }

  @Override
  public String bmmClassName() {
    return "Locatable_ref";
  }

  @Override
  public String toString() {
    return "Locatable_ref";
  }
}
