package org.s2.rm.care.entry;

import java.math.BigDecimal;
import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.DateTime;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Assessment
* BMM ancestors: Care_entry
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Assessment", propOrder = {
  "data",
  "probability",
  "certainty"
})
public class Assessment extends CareEntry {
  /**
  * BMM name: data | BMM type: List<{@literal Node}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "data")
  private @Nullable List<Node> data;

  /**
  * BMM name: probability | BMM type: Decimal
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "probability")
  private BigDecimal probability;

  /**
  * BMM name: certainty | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  * valueConstraint: s2.Certainty
  */
  @XmlElement(name = "certainty")
  private @Nullable TerminologyTerm certainty;

  public Assessment() {}

  public Assessment(Uuid uid, DateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(uid, time, language, subject, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Assessment otherAsAssessment = (Assessment) other;
    return Objects.equals(getQualifiers(), otherAsAssessment.getQualifiers()) &&
      Objects.equals(getGuidelineIds(), otherAsAssessment.getGuidelineIds()) &&
      Objects.equals(getUid(), otherAsAssessment.getUid()) &&
      Objects.equals(getTime(), otherAsAssessment.getTime()) &&
      Objects.equals(getLanguage(), otherAsAssessment.getLanguage()) &&
      Objects.equals(getSubject(), otherAsAssessment.getSubject()) &&
      Objects.equals(getReporter(), otherAsAssessment.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsAssessment.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsAssessment.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsAssessment.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsAssessment.getComment()) &&
      Objects.equals(getCode(), otherAsAssessment.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsAssessment.getOriginalCode()) &&
      Objects.equals(getLinks(), otherAsAssessment.getLinks()) &&
      Objects.equals(getArchetypeNodeId(), otherAsAssessment.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsAssessment.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsAssessment.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsAssessment.getFeederAudit()) &&
      Objects.equals(data, otherAsAssessment.data) &&
      Objects.equals(probability, otherAsAssessment.probability) &&
      Objects.equals(certainty, otherAsAssessment.certainty);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), probability, certainty);
    result = data == null ? 0 : 31 * result + data.hashCode();
    return result;
  }

  public @Nullable List<Node> getData() {
    return data;
  }

  public void setData(@Nullable List<Node> data) {
    this.data = data;
  }

  public BigDecimal getProbability() {
    return probability;
  }

  public void setProbability(BigDecimal probability) {
    this.probability = probability;
  }

  public @Nullable TerminologyTerm getCertainty() {
    return certainty;
  }

  public void setCertainty(@Nullable TerminologyTerm certainty) {
    this.certainty = certainty;
  }

  @Override
  public String bmmClassName() {
    return "Assessment";
  }

  @Override
  public String toString() {
    return "Assessment";
  }
}
