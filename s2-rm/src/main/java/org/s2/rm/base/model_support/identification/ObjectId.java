package main.java.org.s2.rm.base.model_support.identification;

import jakarta.annotation.Nonnull;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Object_id
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Object_id", propOrder = {
  "value"
})
public abstract class ObjectId {
  /**
  * BMM name: value | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "value")
  private @Nonnull String value;

  public ObjectId() {}

  public ObjectId(@Nonnull String value) {
    this.value = value;
  }

  public @Nonnull String getValue() {
    return value;
  }

  public void setValue(@Nonnull String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Object_id";
  }

  @Override
  public String toString() {
    return "Object_id";
  }
}
