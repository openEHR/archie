package org.s2.rm.base.model_support.identification;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Terminology_id
* BMM ancestors: Artifact_id
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Terminology_id")
public class TerminologyId extends ArtifactId {
  public TerminologyId() {}

  public TerminologyId(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    TerminologyId otherAsTerminologyId = (TerminologyId) other;
    return Objects.equals(getValue(), otherAsTerminologyId.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Terminology_id";
  }

  @Override
  public String toString() {
    return "Terminology_id";
  }
}
