package org.s2.rm.entity.physical_entity;

import javax.xml.bind.annotation.*;
import org.s2.rm.entity.entity.Entity;

/**
* BMM name: Physical_entity
* BMM ancestors: Entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Physical_entity")
public abstract class PhysicalEntity extends Entity {
  public PhysicalEntity() {}

  public PhysicalEntity(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Physical_entity";
  }

  @Override
  public String toString() {
    return "Physical_entity";
  }
}
