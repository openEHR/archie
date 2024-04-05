package main.java.org.s2.rm.base.model_support.identification;

import jakarta.annotation.Nonnull;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Artifact_id
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Artifact_id", propOrder = {
  "value"
})
public abstract class ArtifactId {
  /**
  * BMM name: value | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "value")
  private @Nonnull String value;

  public ArtifactId() {}

  public ArtifactId(@Nonnull String value) {
    this.value = value;
  }

  public @Nonnull String getValue() {
    return value;
  }

  public void setValue(@Nonnull String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Artifact_id";
  }

  @Override
  public String toString() {
    return "Artifact_id";
  }
}
