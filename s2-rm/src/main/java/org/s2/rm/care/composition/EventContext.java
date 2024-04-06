package org.s2.rm.care.composition;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.rm.base.patterns.participation.Participation;
import org.s2.rm.base.patterns.participation.PartyIdentified;

/**
* BMM name: Event_context
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Event_context", propOrder = {
  "uid",
  "healthCareFacility",
  "startTime",
  "endTime",
  "participations",
  "location",
  "setting",
  "otherContext"
})
public class EventContext extends Locatable {
  /**
  * BMM name: health_care_facility | BMM type: Party_identified
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "health_care_facility")
  private @Nullable PartyIdentified healthCareFacility;

  /**
  * BMM name: start_time | BMM type: Date_time
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "start_time")
  private RmDateTime startTime;

  /**
  * BMM name: end_time | BMM type: Date_time
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "end_time")
  private @Nullable RmDateTime endTime;

  /**
  * BMM name: participations | BMM type: List<Participation>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "participations")
  private @Nullable List<Participation> participations;

  /**
  * BMM name: location | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "location")
  private @Nullable String location;

  /**
  * BMM name: setting | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "setting")
  private TerminologyTerm setting;

  /**
  * BMM name: other_context | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "other_context")
  private @Nullable List<Node> otherContext;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public EventContext() {}

  public EventContext(RmDateTime startTime, TerminologyTerm setting, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.startTime = startTime;
    this.setting = setting;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    EventContext otherAsEventContext = (EventContext) other;
    return Objects.equals(uid, otherAsEventContext.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsEventContext.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsEventContext.getName()) &&
      Objects.equals(getCode(), otherAsEventContext.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsEventContext.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsEventContext.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsEventContext.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsEventContext.getLinks()) &&
      Objects.equals(healthCareFacility, otherAsEventContext.healthCareFacility) &&
      Objects.equals(startTime, otherAsEventContext.startTime) &&
      Objects.equals(endTime, otherAsEventContext.endTime) &&
      Objects.equals(participations, otherAsEventContext.participations) &&
      Objects.equals(location, otherAsEventContext.location) &&
      Objects.equals(setting, otherAsEventContext.setting) &&
      Objects.equals(otherContext, otherAsEventContext.otherContext);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, healthCareFacility, startTime, endTime, location, setting);
    result = participations == null ? 0 : 31 * result + participations.hashCode();
    result = otherContext == null ? 0 : 31 * result + otherContext.hashCode();
    return result;
  }

  public @Nullable PartyIdentified getHealthCareFacility() {
    return healthCareFacility;
  }

  public void setHealthCareFacility(@Nullable PartyIdentified healthCareFacility) {
    this.healthCareFacility = healthCareFacility;
  }

  public RmDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(RmDateTime startTime) {
    this.startTime = startTime;
  }

  public @Nullable RmDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(@Nullable RmDateTime endTime) {
    this.endTime = endTime;
  }

  public @Nullable List<Participation> getParticipations() {
    return participations;
  }

  public void setParticipations(@Nullable List<Participation> participations) {
    this.participations = participations;
  }

  public @Nullable String getLocation() {
    return location;
  }

  public void setLocation(@Nullable String location) {
    this.location = location;
  }

  public TerminologyTerm getSetting() {
    return setting;
  }

  public void setSetting(TerminologyTerm setting) {
    this.setting = setting;
  }

  public @Nullable List<Node> getOtherContext() {
    return otherContext;
  }

  public void setOtherContext(@Nullable List<Node> otherContext) {
    this.otherContext = otherContext;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Event_context";
  }

  @Override
  public String toString() {
    return "Event_context";
  }
}
