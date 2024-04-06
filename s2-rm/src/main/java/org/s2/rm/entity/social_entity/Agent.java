package org.s2.rm.entity.social_entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Agent
* BMM ancestors: Party
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Agent", propOrder = {
  "personas",
  "languages"
})
public abstract class Agent extends Party {
  /**
  * BMM name: personas | BMM type: List<Persona>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "personas")
  private @Nullable List<Persona> personas;

  /**
  * BMM name: languages | BMM type: List<Terminology_term>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "languages")
  private @Nullable List<TerminologyTerm> languages;

  public Agent() {}

  public Agent(List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(identities, archetypeNodeId, name);
  }

  public @Nullable List<Persona> getPersonas() {
    return personas;
  }

  public void setPersonas(@Nullable List<Persona> personas) {
    this.personas = personas;
  }

  public @Nullable List<TerminologyTerm> getLanguages() {
    return languages;
  }

  public void setLanguages(@Nullable List<TerminologyTerm> languages) {
    this.languages = languages;
  }

  @Override
  public String bmmClassName() {
    return "Agent";
  }

  @Override
  public String toString() {
    return "Agent";
  }
}
