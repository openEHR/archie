package main.java.org.s2.rm.entity.social_entity;

import jakarta.annotation.Nonnull;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Aggregate_agent
* BMM ancestors: Agent
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Aggregate_agent")
public abstract class AggregateAgent extends Agent {
  public AggregateAgent() {}

  public AggregateAgent(@Nonnull List<PartyIdentity> identities, @Nonnull String archetypeNodeId, @Nonnull String name) {
    super(identities, archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Aggregate_agent";
  }

  @Override
  public String toString() {
    return "Aggregate_agent";
  }
}
