package org.s2.rm.base.foundation_types.time;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* Parent of ISO8601 types.
* BMM name: Temporal
* BMM ancestors: Comparable
* isAbstract: true | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Temporal", propOrder = {
  "value"
})
public abstract class Temporal implements Comparable<Temporal> {
  /**
  * BMM name: value | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "value")
  private String value;

  public Temporal() {}

  public Temporal(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Temporal";
  }

  @Override
  public String toString() {
    return "Temporal";
  }
}
