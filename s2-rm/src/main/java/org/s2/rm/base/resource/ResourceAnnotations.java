package org.s2.rm.base.resource;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Resource_annotations
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resource_annotations", propOrder = {
  "documentation"
})
public class ResourceAnnotations {
  /**
  * BMM name: documentation | BMM type: Hash<String,Hash<String,Hash<String,String>>>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "documentation")
  private Map<String, Map<String, Map<String, String>>> documentation;

  public ResourceAnnotations() {}

  public ResourceAnnotations(Map<String, Map<String, Map<String, String>>> documentation) {
    this.documentation = documentation;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ResourceAnnotations otherAsResourceAnnotations = (ResourceAnnotations) other;
    return Objects.equals(documentation, otherAsResourceAnnotations.documentation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), documentation);
  }

  public Map<String, Map<String, Map<String, String>>> getDocumentation() {
    return documentation;
  }

  public void setDocumentation(Map<String, Map<String, Map<String, String>>> documentation) {
    this.documentation = documentation;
  }

  public String bmmClassName() {
    return "Resource_annotations";
  }

  @Override
  public String toString() {
    return "Resource_annotations";
  }
}
