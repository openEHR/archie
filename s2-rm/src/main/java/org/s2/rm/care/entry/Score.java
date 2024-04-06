package org.s2.rm.care.entry;


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
* BMM name: Score
* BMM ancestors: Observation
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Score")
public class Score extends Observation {
  public Score() {}

  public Score(Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
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
      Objects.equals(getArchetypeNodeId(), otherAsScore.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsScore.getName()) &&
      Objects.equals(getCode(), otherAsScore.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsScore.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsScore.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsScore.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsScore.getLinks());
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
