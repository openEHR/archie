package org.s2.rm.entity.social_entity;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.entity.Entity;

/**
* BMM name: Social_entity
* BMM ancestors: Entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
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
