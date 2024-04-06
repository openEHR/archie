package org.s2.rm.base.data_types.quantity;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Temporal_value
* BMM ancestors: Ordered_value
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Temporal_value", propOrder = {
  "magnitude"
})
public abstract class TemporalValue extends OrderedValue {
  /**
  * BMM name: magnitude | BMM type: Temporal
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  // This property is in at least one descendant where it probably has a different type.
  // Skip the property in the parent class (this one).
  // private Temporal magnitude;


  @Override
  public String bmmClassName() {
    return "Temporal_value";
  }

  @Override
  public String toString() {
    return "Temporal_value";
  }
}
