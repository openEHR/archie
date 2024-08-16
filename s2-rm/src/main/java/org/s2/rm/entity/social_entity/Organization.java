package org.s2.rm.entity.social_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Organization
* BMM ancestors: Org_entity
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Organization", propOrder = {
  "uid"
})
public class Organization extends OrgEntity {

  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Organization() {}

  public Organization(List<PartyIdentity> identities, String archetypeNodeId, String name) {
    super(identities, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Organization otherAsOrganization = (Organization) other;
    return Objects.equals(getPersonas(), otherAsOrganization.getPersonas()) &&
      Objects.equals(getLanguages(), otherAsOrganization.getLanguages()) &&
      Objects.equals(getLegalStatus(), otherAsOrganization.getLegalStatus()) &&
      Objects.equals(getIdentities(), otherAsOrganization.getIdentities()) &&
      Objects.equals(getContacts(), otherAsOrganization.getContacts()) &&
      Objects.equals(getAccountabilityTypes(), otherAsOrganization.getAccountabilityTypes()) &&
      Objects.equals(getDescription(), otherAsOrganization.getDescription()) &&
      Objects.equals(uid, otherAsOrganization.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsOrganization.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsOrganization.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsOrganization.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsOrganization.getFeederAudit());
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
    return "Organization";
  }

  @Override
  public String toString() {
    return "Organization";
  }
}
