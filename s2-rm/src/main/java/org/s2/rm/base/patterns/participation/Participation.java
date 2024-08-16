package org.s2.rm.base.patterns.participation;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.foundation_types.interval.Interval;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDateTime;

/**
* BMM name: Participation
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Participation", propOrder = {
  "function",
  "time",
  "mode",
  "performer"
})
public class Participation extends RMObject {
  /**
  * BMM name: function | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  * valueConstraint: s2.ParticipationFunction
  */
  @XmlElement(name = "function")
  private TerminologyTerm function;

  /**
  * BMM name: time | BMM type: Interval<{@literal Date_time}>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "time")
  private @Nullable Interval<RmDateTime> time;

  /**
  * BMM name: mode | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  * valueConstraint: s2.ParticipationMode
  */
  @XmlElement(name = "mode")
  private @Nullable TerminologyTerm mode;

  /**
  * BMM name: performer | BMM type: Party_proxy
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "performer")
  private PartyProxy performer;

  public Participation() {}

  public Participation(TerminologyTerm function, PartyProxy performer) {
    this.function = function;
    this.performer = performer;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Participation otherAsParticipation = (Participation) other;
    return Objects.equals(function, otherAsParticipation.function) &&
      Objects.equals(time, otherAsParticipation.time) &&
      Objects.equals(mode, otherAsParticipation.mode) &&
      Objects.equals(performer, otherAsParticipation.performer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), function, time, mode, performer);
  }

  public TerminologyTerm getFunction() {
    return function;
  }

  public void setFunction(TerminologyTerm function) {
    this.function = function;
  }

  public @Nullable Interval<RmDateTime> getTime() {
    return time;
  }

  public void setTime(@Nullable Interval<RmDateTime> time) {
    this.time = time;
  }

  public @Nullable TerminologyTerm getMode() {
    return mode;
  }

  public void setMode(@Nullable TerminologyTerm mode) {
    this.mode = mode;
  }

  public PartyProxy getPerformer() {
    return performer;
  }

  public void setPerformer(PartyProxy performer) {
    this.performer = performer;
  }

  public String bmmClassName() {
    return "Participation";
  }

  @Override
  public String toString() {
    return "Participation";
  }
}
