package org.s2.rm.base.data_types.quantity;

import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;

/**
* BMM name: Ordered_datum
* BMM ancestors: Data_value
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ordered_datum")
public abstract class OrderedDatum extends DataValue {
  public OrderedDatum() {}


  @Override
  public String bmmClassName() {
    return "Ordered_datum";
  }

  @Override
  public String toString() {
    return "Ordered_datum";
  }
}
