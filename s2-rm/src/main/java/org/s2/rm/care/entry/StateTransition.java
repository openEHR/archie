package org.s2.rm.care.entry;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.text.Text;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: State_transition
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "State_transition", propOrder = {
  "uid",
  "currentState",
  "transition",
  "careflowStep",
  "reason"
})
public class StateTransition extends Locatable {
  /**
  * BMM name: current_state | BMM type: Order_execution_state
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "current_state")
  private OrderExecutionState currentState;

  /**
  * BMM name: transition | BMM type: Order_execution_transition
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "transition")
  private @Nullable OrderExecutionTransition transition;

  /**
  * BMM name: careflow_step | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "careflow_step")
  private @Nullable TerminologyTerm careflowStep;

  /**
  * BMM name: reason | BMM type: List<Text>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "reason")
  private @Nullable List<Text> reason;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public StateTransition() {}

  public StateTransition(OrderExecutionState currentState, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.currentState = currentState;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    StateTransition otherAsStateTransition = (StateTransition) other;
    return Objects.equals(uid, otherAsStateTransition.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsStateTransition.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsStateTransition.getName()) &&
      Objects.equals(getCode(), otherAsStateTransition.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsStateTransition.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsStateTransition.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsStateTransition.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsStateTransition.getLinks()) &&
      Objects.equals(currentState, otherAsStateTransition.currentState) &&
      Objects.equals(transition, otherAsStateTransition.transition) &&
      Objects.equals(careflowStep, otherAsStateTransition.careflowStep) &&
      Objects.equals(reason, otherAsStateTransition.reason);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, currentState, transition, careflowStep);
    result = reason == null ? 0 : 31 * result + reason.hashCode();
    return result;
  }

  public OrderExecutionState getCurrentState() {
    return currentState;
  }

  public void setCurrentState(OrderExecutionState currentState) {
    this.currentState = currentState;
  }

  public @Nullable OrderExecutionTransition getTransition() {
    return transition;
  }

  public void setTransition(@Nullable OrderExecutionTransition transition) {
    this.transition = transition;
  }

  public @Nullable TerminologyTerm getCareflowStep() {
    return careflowStep;
  }

  public void setCareflowStep(@Nullable TerminologyTerm careflowStep) {
    this.careflowStep = careflowStep;
  }

  public @Nullable List<Text> getReason() {
    return reason;
  }

  public void setReason(@Nullable List<Text> reason) {
    this.reason = reason;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "State_transition";
  }

  @Override
  public String toString() {
    return "State_transition";
  }
}
