package org.s2.rm.base.data_types.quantity;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Ordered_value
* BMM ancestors: Ordered_datum
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ordered_value", propOrder = {
  "precision"
})
public abstract class OrderedValue extends OrderedDatum {
  /**
  * BMM name: magnitude | BMM type: Comparable
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  // This property is in at least one descendant where it probably has a different type.
  // Skip the property in the parent class (this one).
  // private Comparable magnitude;

  /**
  * BMM name: precision | BMM type: Integer
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "precision")
  private int precision;


  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }

  @Override
  public String bmmClassName() {
    return "Ordered_value";
  }

  @Override
  public String toString() {
    return "Ordered_value";
  }
}
