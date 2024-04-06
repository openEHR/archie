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
import org.s2.rm.base.model_support.definitions.TrendKind;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Event;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.rm.base.patterns.participation.Participation;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Direct_observation
* BMM ancestors: Observation
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Direct_observation", propOrder = {
  "trend",
  "totalDuration",
  "dataSeries",
  "stateSeries"
})
public class DirectObservation extends Observation {
  /**
  * BMM name: trend | BMM type: Trend_kind
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "trend")
  private @Nullable TrendKind trend;

  /**
  * BMM name: total_duration | BMM type: Duration
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "total_duration")
  private @Nullable RmDuration totalDuration;

  /**
  * BMM name: data_series | BMM type: List<Event>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "data_series")
  private @Nullable List<Event> dataSeries;

  /**
  * BMM name: state_series | BMM type: List<Event>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "state_series")
  private @Nullable List<Event> stateSeries;

  public DirectObservation() {}

  public DirectObservation(Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(uid, time, language, subject, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    DirectObservation otherAsDirectObservation = (DirectObservation) other;
    return Objects.equals(getData(), otherAsDirectObservation.getData()) &&
      Objects.equals(getState(), otherAsDirectObservation.getState()) &&
      Objects.equals(getOrderTracking(), otherAsDirectObservation.getOrderTracking()) &&
      Objects.equals(getQualifiers(), otherAsDirectObservation.getQualifiers()) &&
      Objects.equals(getGuidelineIds(), otherAsDirectObservation.getGuidelineIds()) &&
      Objects.equals(getUid(), otherAsDirectObservation.getUid()) &&
      Objects.equals(getTime(), otherAsDirectObservation.getTime()) &&
      Objects.equals(getLanguage(), otherAsDirectObservation.getLanguage()) &&
      Objects.equals(getSubject(), otherAsDirectObservation.getSubject()) &&
      Objects.equals(getReporter(), otherAsDirectObservation.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsDirectObservation.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsDirectObservation.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsDirectObservation.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsDirectObservation.getComment()) &&
      Objects.equals(getArchetypeNodeId(), otherAsDirectObservation.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsDirectObservation.getName()) &&
      Objects.equals(getCode(), otherAsDirectObservation.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsDirectObservation.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsDirectObservation.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsDirectObservation.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsDirectObservation.getLinks()) &&
      Objects.equals(trend, otherAsDirectObservation.trend) &&
      Objects.equals(totalDuration, otherAsDirectObservation.totalDuration) &&
      Objects.equals(dataSeries, otherAsDirectObservation.dataSeries) &&
      Objects.equals(stateSeries, otherAsDirectObservation.stateSeries);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), trend, totalDuration);
    result = dataSeries == null ? 0 : 31 * result + dataSeries.hashCode();
    result = stateSeries == null ? 0 : 31 * result + stateSeries.hashCode();
    return result;
  }

  public @Nullable TrendKind getTrend() {
    return trend;
  }

  public void setTrend(@Nullable TrendKind trend) {
    this.trend = trend;
  }

  public @Nullable RmDuration getTotalDuration() {
    return totalDuration;
  }

  public void setTotalDuration(@Nullable RmDuration totalDuration) {
    this.totalDuration = totalDuration;
  }

  public @Nullable List<Event> getDataSeries() {
    return dataSeries;
  }

  public void setDataSeries(@Nullable List<Event> dataSeries) {
    this.dataSeries = dataSeries;
  }

  public @Nullable List<Event> getStateSeries() {
    return stateSeries;
  }

  public void setStateSeries(@Nullable List<Event> stateSeries) {
    this.stateSeries = stateSeries;
  }

  @Override
  public String bmmClassName() {
    return "Direct_observation";
  }

  @Override
  public String toString() {
    return "Direct_observation";
  }
}
