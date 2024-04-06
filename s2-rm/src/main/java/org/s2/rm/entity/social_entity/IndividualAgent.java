package org.s2.rm.entity.social_entity;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Individual_agent
* BMM ancestors: Agent
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Individual_agent")
public abstract class IndividualAgent extends Agent {
  public IndividualAgent() {}

  public IndividualAgent(List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(identities, archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Individual_agent";
  }

  @Override
  public String toString() {
    return "Individual_agent";
  }
}
