package main.java.org.s2.rm.base.model_support.archetyped;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Pathable
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Pathable")
public abstract class Pathable {

  public String bmmClassName() {
    return "Pathable";
  }

  @Override
  public String toString() {
    return "Pathable";
  }
}
