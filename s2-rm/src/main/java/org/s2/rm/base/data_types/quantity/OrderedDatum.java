package org.s2.rm.base.data_types.quantity;

import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;

/**
* BMM name: Ordered_datum
* BMM ancestors: Data_value
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ordered_datum")
public abstract class OrderedDatum extends DataValue {

  @Override
  public String bmmClassName() {
    return "Ordered_datum";
  }

  @Override
  public String toString() {
    return "Ordered_datum";
  }
}
