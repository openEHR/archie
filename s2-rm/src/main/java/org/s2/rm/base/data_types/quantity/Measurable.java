package org.s2.rm.base.data_types.quantity;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Measurable
* BMM ancestors: Ordered_value
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Measurable", propOrder = {
  "magnitude"
})
public abstract class Measurable extends OrderedValue {
  /**
  * BMM name: magnitude | BMM type: Numeric
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  // This property is in at least one descendant where it probably has a different type.
  // Skip the property in the parent class (this one).
  // private @Nullable Numeric magnitude;


  @Override
  public String bmmClassName() {
    return "Measurable";
  }

  @Override
  public String toString() {
    return "Measurable";
  }
}
