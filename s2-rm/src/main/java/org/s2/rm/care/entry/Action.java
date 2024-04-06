package org.s2.rm.care.entry;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.text.Text;
import org.s2.rm.base.foundation_types.primitive_types.Uri;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.foundation_types.time.RmDuration;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.rm.base.patterns.participation.Participation;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Action
* BMM ancestors: Care_act_entry
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Action", propOrder = {
  "activityId",
  "duration",
  "description",
  "stateTransition"
})
public class Action extends CareActEntry {
  /**
  * BMM name: activity_id | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "activity_id")
  private String activityId;

  /**
  * BMM name: duration | BMM type: Duration
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "duration")
  private @Nullable RmDuration duration;

  /**
  * BMM name: description | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "description")
  private @Nullable List<Node> description;

  /**
  * BMM name: state_transition | BMM type: State_transition
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "state_transition")
  private @Nullable StateTransition stateTransition;

  public Action() {}

  public Action(String activityId, Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(uid, time, language, subject, archetypeNodeId, name);
    this.activityId = activityId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Action otherAsAction = (Action) other;
    return Objects.equals(getOrderTracking(), otherAsAction.getOrderTracking()) &&
      Objects.equals(getQualifiers(), otherAsAction.getQualifiers()) &&
      Objects.equals(getGuidelineIds(), otherAsAction.getGuidelineIds()) &&
      Objects.equals(getUid(), otherAsAction.getUid()) &&
      Objects.equals(getTime(), otherAsAction.getTime()) &&
      Objects.equals(getLanguage(), otherAsAction.getLanguage()) &&
      Objects.equals(getSubject(), otherAsAction.getSubject()) &&
      Objects.equals(getReporter(), otherAsAction.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsAction.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsAction.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsAction.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsAction.getComment()) &&
      Objects.equals(getArchetypeNodeId(), otherAsAction.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsAction.getName()) &&
      Objects.equals(getCode(), otherAsAction.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsAction.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsAction.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsAction.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsAction.getLinks()) &&
      Objects.equals(activityId, otherAsAction.activityId) &&
      Objects.equals(duration, otherAsAction.duration) &&
      Objects.equals(description, otherAsAction.description) &&
      Objects.equals(stateTransition, otherAsAction.stateTransition);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), activityId, duration, stateTransition);
    result = description == null ? 0 : 31 * result + description.hashCode();
    return result;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public @Nullable RmDuration getDuration() {
    return duration;
  }

  public void setDuration(@Nullable RmDuration duration) {
    this.duration = duration;
  }

  public @Nullable List<Node> getDescription() {
    return description;
  }

  public void setDescription(@Nullable List<Node> description) {
    this.description = description;
  }

  public @Nullable StateTransition getStateTransition() {
    return stateTransition;
  }

  public void setStateTransition(@Nullable StateTransition stateTransition) {
    this.stateTransition = stateTransition;
  }

  @Override
  public String bmmClassName() {
    return "Action";
  }

  @Override
  public String toString() {
    return "Action";
  }
}
