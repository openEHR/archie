package org.s2.rm.entity.social_entity;

import javax.xml.bind.annotation.*;
import org.s2.rm.entity.entity.Entity;

/**
* BMM name: Social_entity
* BMM ancestors: Entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Social_entity")
public abstract class SocialEntity extends Entity {
  public SocialEntity() {}

  public SocialEntity(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Social_entity";
  }

  @Override
  public String toString() {
    return "Social_entity";
  }
}