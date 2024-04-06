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
* BMM name: Imaging
* BMM ancestors: Indirect_observation
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Imaging")
public class Imaging extends IndirectObservation {
  public Imaging() {}

  public Imaging(RmDateTime resultTime, Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(resultTime, uid, time, language, subject, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Imaging otherAsImaging = (Imaging) other;
    return Objects.equals(getResultTime(), otherAsImaging.getResultTime()) &&
      Objects.equals(getData(), otherAsImaging.getData()) &&
      Objects.equals(getState(), otherAsImaging.getState()) &&
      Objects.equals(getOrderTracking(), otherAsImaging.getOrderTracking()) &&
      Objects.equals(getQualifiers(), otherAsImaging.getQualifiers()) &&
      Objects.equals(getGuidelineIds(), otherAsImaging.getGuidelineIds()) &&
      Objects.equals(getUid(), otherAsImaging.getUid()) &&
      Objects.equals(getTime(), otherAsImaging.getTime()) &&
      Objects.equals(getLanguage(), otherAsImaging.getLanguage()) &&
      Objects.equals(getSubject(), otherAsImaging.getSubject()) &&
      Objects.equals(getReporter(), otherAsImaging.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsImaging.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsImaging.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsImaging.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsImaging.getComment()) &&
      Objects.equals(getArchetypeNodeId(), otherAsImaging.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsImaging.getName()) &&
      Objects.equals(getCode(), otherAsImaging.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsImaging.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsImaging.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsImaging.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsImaging.getLinks());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Imaging";
  }

  @Override
  public String toString() {
    return "Imaging";
  }
}
