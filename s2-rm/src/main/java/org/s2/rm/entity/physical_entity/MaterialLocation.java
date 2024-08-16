package org.s2.rm.entity.physical_entity;

import javax.xml.bind.annotation.*;

/**
* BMM name: Material_location
* BMM ancestors: Materially_dependent_entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Material_location")
public abstract class MaterialLocation extends MateriallyDependentEntity {
  public MaterialLocation() {}

  public MaterialLocation(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Material_location";
  }

  @Override
  public String toString() {
    return "Material_location";
  }
}
