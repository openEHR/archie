package org.s2.rm.base.patterns.participation;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.RweIdRef;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.identification.ObjectRef;

/**
* BMM name: Party_related
* BMM ancestors: Party_identified
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party_related", propOrder = {
  "relationship"
})
public class PartyRelated extends PartyIdentified {
  /**
  * BMM name: relationship | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "relationship")
  private TerminologyTerm relationship;

  public PartyRelated() {}

  public PartyRelated(TerminologyTerm relationship) {
    this.relationship = relationship;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PartyRelated otherAsPartyRelated = (PartyRelated) other;
    return Objects.equals(getName(), otherAsPartyRelated.getName()) &&
      Objects.equals(getIdentifiers(), otherAsPartyRelated.getIdentifiers()) &&
      Objects.equals(getExternalRef(), otherAsPartyRelated.getExternalRef()) &&
      Objects.equals(relationship, otherAsPartyRelated.relationship);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), relationship);
  }

  public TerminologyTerm getRelationship() {
    return relationship;
  }

  public void setRelationship(TerminologyTerm relationship) {
    this.relationship = relationship;
  }

  @Override
  public String bmmClassName() {
    return "Party_related";
  }

  @Override
  public String toString() {
    return "Party_related";
  }
}
