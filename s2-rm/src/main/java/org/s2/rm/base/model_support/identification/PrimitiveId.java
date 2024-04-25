package org.s2.rm.base.model_support.identification;


import com.nedap.archie.base.RMObject;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Primitive_id
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Primitive_id", propOrder = {
  "value"
})
public abstract class PrimitiveId extends RMObject {
  /**
  * BMM name: value | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "value")
  private String value;

  public PrimitiveId() {}

  public PrimitiveId(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Primitive_id";
  }

  @Override
  public String toString() {
    return "Primitive_id";
  }
}
