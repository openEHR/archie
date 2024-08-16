package org.s2.rm.entity.physical_entity;

import javax.xml.bind.annotation.*;

/**
* BMM name: Material_entity
* BMM ancestors: Materially_dependent_entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Material_entity")
public abstract class MaterialEntity extends MateriallyDependentEntity {
  public MaterialEntity() {}

  public MaterialEntity(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Material_entity";
  }

  @Override
  public String toString() {
    return "Material_entity";
  }
}
