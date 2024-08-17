package org.s2.rm.care.entry;

import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.time.DateTime;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Score
* BMM ancestors: Observation
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Score")
public class Score extends Observation {
  public Score() {}

  public Score(Uuid uid, DateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(uid, time, language, subject, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Score otherAsScore = (Score) other;
    return Objects.equals(getData(), otherAsScore.getData()) &&
      Objects.equals(getState(), otherAsScore.getState()) &&
      Objects.equals(getOrderTracking(), otherAsScore.getOrderTracking()) &&
      Objects.equals(getQualifiers(), otherAsScore.getQualifiers()) &&
      Objects.equals(getGuidelineIds(), otherAsScore.getGuidelineIds()) &&
      Objects.equals(getUid(), otherAsScore.getUid()) &&
      Objects.equals(getTime(), otherAsScore.getTime()) &&
      Objects.equals(getLanguage(), otherAsScore.getLanguage()) &&
      Objects.equals(getSubject(), otherAsScore.getSubject()) &&
      Objects.equals(getReporter(), otherAsScore.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsScore.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsScore.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsScore.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsScore.getComment()) &&
      Objects.equals(getCode(), otherAsScore.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsScore.getOriginalCode()) &&
      Objects.equals(getLinks(), otherAsScore.getLinks()) &&
      Objects.equals(getArchetypeNodeId(), otherAsScore.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsScore.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsScore.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsScore.getFeederAudit());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Score";
  }

  @Override
  public String toString() {
    return "Score";
  }
}
