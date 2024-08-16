package org.s2.rm.entity.entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Entity
* BMM ancestors: Locatable
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Entity", propOrder = {
  "description"
})
public abstract class Entity extends Locatable {
  /**
  * BMM name: description | BMM type: List<{@literal Node}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "description")
  private @Nullable List<Node> description;

  public Entity() {}

  public Entity(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  public @Nullable List<Node> getDescription() {
    return description;
  }

  public void setDescription(@Nullable List<Node> description) {
    this.description = description;
  }

  @Override
  public String bmmClassName() {
    return "Entity";
  }

  @Override
  public String toString() {
    return "Entity";
  }
}
