package org.s2.rm.entity.social_entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Org_unit
* BMM ancestors: Org_entity
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Org_unit", propOrder = {
  "uid"
})
public class OrgUnit extends OrgEntity {

  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public OrgUnit() {}

  public OrgUnit(List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(identities, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    OrgUnit otherAsOrgUnit = (OrgUnit) other;
    return Objects.equals(getPersonas(), otherAsOrgUnit.getPersonas()) &&
      Objects.equals(getLanguages(), otherAsOrgUnit.getLanguages()) &&
      Objects.equals(getLegalStatus(), otherAsOrgUnit.getLegalStatus()) &&
      Objects.equals(getIdentities(), otherAsOrgUnit.getIdentities()) &&
      Objects.equals(getContacts(), otherAsOrgUnit.getContacts()) &&
      Objects.equals(getAccountabilityTypes(), otherAsOrgUnit.getAccountabilityTypes()) &&
      Objects.equals(getDescription(), otherAsOrgUnit.getDescription()) &&
      Objects.equals(uid, otherAsOrgUnit.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsOrgUnit.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsOrgUnit.getName()) &&
      Objects.equals(getCode(), otherAsOrgUnit.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsOrgUnit.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsOrgUnit.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsOrgUnit.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsOrgUnit.getLinks());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid);
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Org_unit";
  }

  @Override
  public String toString() {
    return "Org_unit";
  }
}
