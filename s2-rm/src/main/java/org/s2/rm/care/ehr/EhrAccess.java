package org.s2.rm.care.ehr;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Ehr_access
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ehr_access", propOrder = {
  "uid",
  "settings"
})
public class EhrAccess extends Locatable {
  /**
  * BMM name: settings | BMM type: Access_control_settings
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "settings")
  private @Nullable AccessControlSettings settings;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public EhrAccess() {}

  public EhrAccess(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    EhrAccess otherAsEhrAccess = (EhrAccess) other;
    return Objects.equals(uid, otherAsEhrAccess.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsEhrAccess.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsEhrAccess.getName()) &&
      Objects.equals(getCode(), otherAsEhrAccess.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsEhrAccess.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsEhrAccess.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsEhrAccess.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsEhrAccess.getLinks()) &&
      Objects.equals(settings, otherAsEhrAccess.settings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid, settings);
  }

  public @Nullable AccessControlSettings getSettings() {
    return settings;
  }

  public void setSettings(@Nullable AccessControlSettings settings) {
    this.settings = settings;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Ehr_access";
  }

  @Override
  public String toString() {
    return "Ehr_access";
  }
}
