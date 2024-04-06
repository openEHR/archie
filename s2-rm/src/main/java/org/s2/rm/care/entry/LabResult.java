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
* BMM name: Lab_result
* BMM ancestors: Indirect_observation
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Lab_result")
public class LabResult extends IndirectObservation {
  public LabResult() {}

  public LabResult(RmDateTime resultTime, Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(resultTime, uid, time, language, subject, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    LabResult otherAsLabResult = (LabResult) other;
    return Objects.equals(getResultTime(), otherAsLabResult.getResultTime()) &&
      Objects.equals(getData(), otherAsLabResult.getData()) &&
      Objects.equals(getState(), otherAsLabResult.getState()) &&
      Objects.equals(getOrderTracking(), otherAsLabResult.getOrderTracking()) &&
      Objects.equals(getQualifiers(), otherAsLabResult.getQualifiers()) &&
      Objects.equals(getGuidelineIds(), otherAsLabResult.getGuidelineIds()) &&
      Objects.equals(getUid(), otherAsLabResult.getUid()) &&
      Objects.equals(getTime(), otherAsLabResult.getTime()) &&
      Objects.equals(getLanguage(), otherAsLabResult.getLanguage()) &&
      Objects.equals(getSubject(), otherAsLabResult.getSubject()) &&
      Objects.equals(getReporter(), otherAsLabResult.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsLabResult.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsLabResult.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsLabResult.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsLabResult.getComment()) &&
      Objects.equals(getArchetypeNodeId(), otherAsLabResult.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsLabResult.getName()) &&
      Objects.equals(getCode(), otherAsLabResult.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsLabResult.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsLabResult.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsLabResult.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsLabResult.getLinks());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Lab_result";
  }

  @Override
  public String toString() {
    return "Lab_result";
  }
}
