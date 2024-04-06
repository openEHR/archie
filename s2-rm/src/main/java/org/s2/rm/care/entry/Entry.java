package org.s2.rm.care.entry;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.text.Text;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.participation.Participation;
import org.s2.rm.base.patterns.participation.PartyProxy;
import org.s2.rm.care.composition.ContentItem;

/**
* BMM name: Entry
* BMM ancestors: Content_item
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Entry", propOrder = {
  "uid",
  "time",
  "language",
  "subject",
  "reporter",
  "authorizationActions",
  "otherParticipations",
  "workflowId",
  "comment"
})
public abstract class Entry extends ContentItem {
  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "uid")
  private Uuid uid;

  /**
  * BMM name: time | BMM type: Date_time
  * isMandatory: true | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "time")
  private RmDateTime time;

  /**
  * BMM name: language | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "language")
  private TerminologyCode language;

  /**
  * BMM name: subject | BMM type: Party_proxy
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "subject")
  private PartyProxy subject;

  /**
  * BMM name: reporter | BMM type: Party_proxy
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "reporter")
  private @Nullable PartyProxy reporter;

  /**
  * BMM name: authorization_actions | BMM type: List<Participation>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "authorization_actions")
  private @Nullable List<Participation> authorizationActions;

  /**
  * BMM name: other_participations | BMM type: List<Participation>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "other_participations")
  private @Nullable List<Participation> otherParticipations;

  /**
  * BMM name: workflow_id | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "workflow_id")
  private @Nullable String workflowId;

  /**
  * BMM name: comment | BMM type: Text
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "comment")
  private @Nullable Text comment;

  public Entry() {}

  public Entry(Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.uid = uid;
    this.time = time;
    this.language = language;
    this.subject = subject;
  }

  public Uuid getUid() {
    return uid;
  }

  public void setUid(Uuid uid) {
    this.uid = uid;
  }

  public RmDateTime getTime() {
    return time;
  }

  public void setTime(RmDateTime time) {
    this.time = time;
  }

  public TerminologyCode getLanguage() {
    return language;
  }

  public void setLanguage(TerminologyCode language) {
    this.language = language;
  }

  public PartyProxy getSubject() {
    return subject;
  }

  public void setSubject(PartyProxy subject) {
    this.subject = subject;
  }

  public @Nullable PartyProxy getReporter() {
    return reporter;
  }

  public void setReporter(@Nullable PartyProxy reporter) {
    this.reporter = reporter;
  }

  public @Nullable List<Participation> getAuthorizationActions() {
    return authorizationActions;
  }

  public void setAuthorizationActions(@Nullable List<Participation> authorizationActions) {
    this.authorizationActions = authorizationActions;
  }

  public @Nullable List<Participation> getOtherParticipations() {
    return otherParticipations;
  }

  public void setOtherParticipations(@Nullable List<Participation> otherParticipations) {
    this.otherParticipations = otherParticipations;
  }

  public @Nullable String getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(@Nullable String workflowId) {
    this.workflowId = workflowId;
  }

  public @Nullable Text getComment() {
    return comment;
  }

  public void setComment(@Nullable Text comment) {
    this.comment = comment;
  }

  @Override
  public String bmmClassName() {
    return "Entry";
  }

  @Override
  public String toString() {
    return "Entry";
  }
}
