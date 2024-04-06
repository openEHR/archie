package org.s2.rm.care.entry;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.text.Text;
import org.s2.rm.base.foundation_types.primitive_types.Uri;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.rm.base.patterns.participation.Participation;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Assessment
* BMM ancestors: Care_entry
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Assessment", propOrder = {
  "data",
  "probability",
  "certainty"
})
public class Assessment extends CareEntry {
  /**
  * BMM name: data | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "data")
  private @Nullable List<Node> data;

  /**
  * BMM name: probability | BMM type: Real
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "probability")
  private double probability;

  /**
  * BMM name: certainty | BMM type: Terminology_code
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "certainty")
  private @Nullable TerminologyCode certainty;

  public Assessment() {}

  public Assessment(Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
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
      Objects.equals(getArchetypeNodeId(), otherAsAssessment.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsAssessment.getName()) &&
      Objects.equals(getCode(), otherAsAssessment.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsAssessment.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsAssessment.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsAssessment.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsAssessment.getLinks()) &&
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

  public double getProbability() {
    return probability;
  }

  public void setProbability(double probability) {
    this.probability = probability;
  }

  public @Nullable TerminologyCode getCertainty() {
    return certainty;
  }

  public void setCertainty(@Nullable TerminologyCode certainty) {
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
