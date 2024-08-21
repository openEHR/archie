package org.s2.rm.entity.social_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.interval.Interval;
import org.s2.rm.base.foundation_types.time.RmDate;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Capability
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Capability", propOrder = {
  "uid",
  "credentials",
  "timeValidity"
})
public class Capability extends Locatable {
  /**
  * BMM name: credentials | BMM type: {@code List<Node>}
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "credentials")
  private @Nullable List<Node> credentials;

  /**
  * BMM name: time_validity | BMM type: {@code Interval<Date>}
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "time_validity")
  private @Nullable Interval<RmDate> timeValidity;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Capability() {}

  public Capability(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Capability otherAsCapability = (Capability) other;
    return Objects.equals(uid, otherAsCapability.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsCapability.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsCapability.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsCapability.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsCapability.getFeederAudit()) &&
      Objects.equals(credentials, otherAsCapability.credentials) &&
      Objects.equals(timeValidity, otherAsCapability.timeValidity);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, timeValidity);
    result = credentials == null ? 0 : 31 * result + credentials.hashCode();
    return result;
  }

  public @Nullable List<Node> getCredentials() {
    return credentials;
  }

  public void setCredentials(@Nullable List<Node> credentials) {
    this.credentials = credentials;
  }

  public @Nullable Interval<RmDate> getTimeValidity() {
    return timeValidity;
  }

  public void setTimeValidity(@Nullable Interval<RmDate> timeValidity) {
    this.timeValidity = timeValidity;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Capability";
  }

  @Override
  public String toString() {
    return "Capability";
  }
}
