package main.java.org.s2.rm.base.patterns.participation;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.interval.Interval;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDateTime;

/**
* BMM name: Participation
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Participation", propOrder = {
  "function",
  "time",
  "mode",
  "performer"
})
public class Participation {
  /**
  * BMM name: function | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "function")
  private @Nonnull String function;

  /**
  * BMM name: time | BMM type: Interval<Date_time>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "time")
  private @Nullable Interval<RmDateTime> time;

  /**
  * BMM name: mode | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "mode")
  private @Nullable TerminologyTerm mode;

  /**
  * BMM name: performer | BMM type: Party_proxy
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "performer")
  private @Nonnull PartyProxy performer;

  public Participation() {}

  public Participation(@Nonnull String function, @Nonnull PartyProxy performer) {
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

  public @Nonnull String getFunction() {
    return function;
  }

  public void setFunction(@Nonnull String function) {
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

  public @Nonnull PartyProxy getPerformer() {
    return performer;
  }

  public void setPerformer(@Nonnull PartyProxy performer) {
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
