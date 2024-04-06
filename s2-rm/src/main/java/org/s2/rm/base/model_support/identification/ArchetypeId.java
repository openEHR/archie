package org.s2.rm.base.model_support.identification;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Archetype_id
* BMM ancestors: Artifact_id
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Archetype_id")
public class ArchetypeId extends ArtifactId {
  public ArchetypeId() {}

  public ArchetypeId(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ArchetypeId otherAsArchetypeId = (ArchetypeId) other;
    return Objects.equals(getValue(), otherAsArchetypeId.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Archetype_id";
  }

  @Override
  public String toString() {
    return "Archetype_id";
  }
}
