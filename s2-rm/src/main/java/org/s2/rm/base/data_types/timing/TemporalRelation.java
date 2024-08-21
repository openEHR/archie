package org.s2.rm.base.data_types.timing;

import org.s2.util.enumerations.EnumerationVar;
import org.s2.util.enumerations.IntegerEnumerationVar;

import javax.xml.bind.annotation.*;

/**
* BMM name: Temporal_relation
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Temporal_relation", propOrder = {"value"})
public class TemporalRelation extends IntegerEnumerationVar<TemporalRelationEnum> {

  public TemporalRelation() {
    this.value = TemporalRelationEnum.getInstance().getItemValue(0);
  }

  // Enumeration value constructor.
  public TemporalRelation(int value) {
    this.value = value;
  }

  public String bmmClassName() {
    return "Temporal_relation";
  }

  @Override
  public String toString() {
    return "Temporal_relation";
  }
}
