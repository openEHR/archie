package org.s2.rm.base.model_support.archetyped;

import com.nedap.archie.base.RMObject;
import javax.xml.bind.annotation.*;

/**
* BMM name: Pathable
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Pathable")
public abstract class Pathable extends RMObject {
  public Pathable() {}


  public String bmmClassName() {
    return "Pathable";
  }

  @Override
  public String toString() {
    return "Pathable";
  }
}
