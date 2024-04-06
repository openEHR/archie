package org.s2.rm.base.data_types.timing;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDuration;

/**
* BMM name: Customary_time
* BMM ancestors: Hour_specifier
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Customary_time", propOrder = {
  "referenceEvent",
  "temporalRelation"
})
public class CustomaryTime extends HourSpecifier {
  /**
  * BMM name: reference_event | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "reference_event")
  private TerminologyTerm referenceEvent;

  /**
  * BMM name: temporal_relation | BMM type: Temporal_relation
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "temporal_relation")
  private @Nullable TemporalRelation temporalRelation;

  public CustomaryTime() {}

  public CustomaryTime(TerminologyTerm referenceEvent) {
    this.referenceEvent = referenceEvent;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    CustomaryTime otherAsCustomaryTime = (CustomaryTime) other;
    return Objects.equals(getDuration(), otherAsCustomaryTime.getDuration()) &&
      Objects.equals(referenceEvent, otherAsCustomaryTime.referenceEvent) &&
      Objects.equals(temporalRelation, otherAsCustomaryTime.temporalRelation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), referenceEvent, temporalRelation);
  }

  public TerminologyTerm getReferenceEvent() {
    return referenceEvent;
  }

  public void setReferenceEvent(TerminologyTerm referenceEvent) {
    this.referenceEvent = referenceEvent;
  }

  public @Nullable TemporalRelation getTemporalRelation() {
    return temporalRelation;
  }

  public void setTemporalRelation(@Nullable TemporalRelation temporalRelation) {
    this.temporalRelation = temporalRelation;
  }

  @Override
  public String bmmClassName() {
    return "Customary_time";
  }

  @Override
  public String toString() {
    return "Customary_time";
  }
}
