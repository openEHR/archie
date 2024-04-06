package org.s2.rm.entity.social_entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Automaton
* BMM ancestors: Individual_agent
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Automaton", propOrder = {
  "uid"
})
public class Automaton extends IndividualAgent {

  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Automaton() {}

  public Automaton(List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(identities, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Automaton otherAsAutomaton = (Automaton) other;
    return Objects.equals(getPersonas(), otherAsAutomaton.getPersonas()) &&
      Objects.equals(getLanguages(), otherAsAutomaton.getLanguages()) &&
      Objects.equals(getLegalStatus(), otherAsAutomaton.getLegalStatus()) &&
      Objects.equals(getIdentities(), otherAsAutomaton.getIdentities()) &&
      Objects.equals(getContacts(), otherAsAutomaton.getContacts()) &&
      Objects.equals(getAccountabilityTypes(), otherAsAutomaton.getAccountabilityTypes()) &&
      Objects.equals(getDescription(), otherAsAutomaton.getDescription()) &&
      Objects.equals(uid, otherAsAutomaton.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsAutomaton.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsAutomaton.getName()) &&
      Objects.equals(getCode(), otherAsAutomaton.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsAutomaton.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsAutomaton.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsAutomaton.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsAutomaton.getLinks());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid);
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Automaton";
  }

  @Override
  public String toString() {
    return "Automaton";
  }
}
