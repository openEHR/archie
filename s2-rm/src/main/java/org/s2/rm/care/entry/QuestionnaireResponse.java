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
* Entry subtype used questionnaire responses
* BMM name: Questionnaire_response
* BMM ancestors: Observation
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Questionnaire_response", propOrder = {
  "questionnaireIdentifier",
  "questionnaireVersion",
  "questionnaireTitle",
  "completionStatus"
})
public class QuestionnaireResponse extends Observation {
  /**
  * BMM name: questionnaire_identifier | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "questionnaire_identifier")
  private String questionnaireIdentifier;

  /**
  * BMM name: questionnaire_version | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "questionnaire_version")
  private @Nullable String questionnaireVersion;

  /**
  * BMM name: questionnaire_title | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "questionnaire_title")
  private TerminologyTerm questionnaireTitle;

  /**
  * BMM name: completion_status | BMM type: Terminology_code
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "completion_status")
  private @Nullable TerminologyCode completionStatus;

  public QuestionnaireResponse() {}

  public QuestionnaireResponse(String questionnaireIdentifier, TerminologyTerm questionnaireTitle, Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(uid, time, language, subject, archetypeNodeId, name);
    this.questionnaireIdentifier = questionnaireIdentifier;
    this.questionnaireTitle = questionnaireTitle;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    QuestionnaireResponse otherAsQuestionnaireResponse = (QuestionnaireResponse) other;
    return Objects.equals(getData(), otherAsQuestionnaireResponse.getData()) &&
      Objects.equals(getState(), otherAsQuestionnaireResponse.getState()) &&
      Objects.equals(getOrderTracking(), otherAsQuestionnaireResponse.getOrderTracking()) &&
      Objects.equals(getQualifiers(), otherAsQuestionnaireResponse.getQualifiers()) &&
      Objects.equals(getGuidelineIds(), otherAsQuestionnaireResponse.getGuidelineIds()) &&
      Objects.equals(getUid(), otherAsQuestionnaireResponse.getUid()) &&
      Objects.equals(getTime(), otherAsQuestionnaireResponse.getTime()) &&
      Objects.equals(getLanguage(), otherAsQuestionnaireResponse.getLanguage()) &&
      Objects.equals(getSubject(), otherAsQuestionnaireResponse.getSubject()) &&
      Objects.equals(getReporter(), otherAsQuestionnaireResponse.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsQuestionnaireResponse.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsQuestionnaireResponse.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsQuestionnaireResponse.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsQuestionnaireResponse.getComment()) &&
      Objects.equals(getArchetypeNodeId(), otherAsQuestionnaireResponse.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsQuestionnaireResponse.getName()) &&
      Objects.equals(getCode(), otherAsQuestionnaireResponse.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsQuestionnaireResponse.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsQuestionnaireResponse.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsQuestionnaireResponse.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsQuestionnaireResponse.getLinks()) &&
      Objects.equals(questionnaireIdentifier, otherAsQuestionnaireResponse.questionnaireIdentifier) &&
      Objects.equals(questionnaireVersion, otherAsQuestionnaireResponse.questionnaireVersion) &&
      Objects.equals(questionnaireTitle, otherAsQuestionnaireResponse.questionnaireTitle) &&
      Objects.equals(completionStatus, otherAsQuestionnaireResponse.completionStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), questionnaireIdentifier, questionnaireVersion, questionnaireTitle, completionStatus);
  }

  public String getQuestionnaireIdentifier() {
    return questionnaireIdentifier;
  }

  public void setQuestionnaireIdentifier(String questionnaireIdentifier) {
    this.questionnaireIdentifier = questionnaireIdentifier;
  }

  public @Nullable String getQuestionnaireVersion() {
    return questionnaireVersion;
  }

  public void setQuestionnaireVersion(@Nullable String questionnaireVersion) {
    this.questionnaireVersion = questionnaireVersion;
  }

  public TerminologyTerm getQuestionnaireTitle() {
    return questionnaireTitle;
  }

  public void setQuestionnaireTitle(TerminologyTerm questionnaireTitle) {
    this.questionnaireTitle = questionnaireTitle;
  }

  public @Nullable TerminologyCode getCompletionStatus() {
    return completionStatus;
  }

  public void setCompletionStatus(@Nullable TerminologyCode completionStatus) {
    this.completionStatus = completionStatus;
  }

  @Override
  public String bmmClassName() {
    return "Questionnaire_response";
  }

  @Override
  public String toString() {
    return "Questionnaire_response";
  }
}
