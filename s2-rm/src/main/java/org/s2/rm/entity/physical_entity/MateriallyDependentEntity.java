package org.s2.rm.entity.physical_entity;

import javax.xml.bind.annotation.*;

/**
* BMM name: Materially_dependent_entity
* BMM ancestors: Physical_entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Materially_dependent_entity")
public abstract class MateriallyDependentEntity extends PhysicalEntity {
  public MateriallyDependentEntity() {}

  public MateriallyDependentEntity(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Materially_dependent_entity";
  }

  @Override
  public String toString() {
    return "Materially_dependent_entity";
  }
}
