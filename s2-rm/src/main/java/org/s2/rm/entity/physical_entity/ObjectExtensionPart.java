package org.s2.rm.entity.physical_entity;

import javax.xml.bind.annotation.*;

/**
* BMM name: Object_extension_part
* BMM ancestors: Material_entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Object_extension_part")
public abstract class ObjectExtensionPart extends MaterialEntity {
  /**
  * BMM name: parts | BMM type: {@code List<Object_extension_part>}
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  // This property is in at least one descendant where it probably has a different type.
  // Skip the property in the parent class (this one).
  // private @Nullable List<ObjectExtensionPart> parts;

  public ObjectExtensionPart() {}

  public ObjectExtensionPart(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Object_extension_part";
  }

  @Override
  public String toString() {
    return "Object_extension_part";
  }
}
