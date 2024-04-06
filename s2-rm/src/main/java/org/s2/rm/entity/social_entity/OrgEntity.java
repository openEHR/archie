package org.s2.rm.entity.social_entity;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Org_entity
* BMM ancestors: Aggregate_agent
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Org_entity")
public abstract class OrgEntity extends AggregateAgent {
  public OrgEntity() {}

  public OrgEntity(List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(identities, archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Org_entity";
  }

  @Override
  public String toString() {
    return "Org_entity";
  }
}
