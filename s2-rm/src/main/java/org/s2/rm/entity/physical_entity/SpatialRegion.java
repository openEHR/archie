package org.s2.rm.entity.physical_entity;

import javax.xml.bind.annotation.*;

/**
* BMM name: Spatial_region
* BMM ancestors: Physical_entity
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spatial_region")
public abstract class SpatialRegion extends PhysicalEntity {
  public SpatialRegion() {}

  public SpatialRegion(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Spatial_region";
  }

  @Override
  public String toString() {
    return "Spatial_region";
  }
}
