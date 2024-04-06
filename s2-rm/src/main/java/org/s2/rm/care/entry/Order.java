package org.s2.rm.care.entry;


import javax.annotation.Nullable;;
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
* BMM name: Order
* BMM ancestors: Care_act_entry
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order", propOrder = {
  "narrative",
  "expiryTime",
  "activities"
})
public class Order extends CareActEntry {
  /**
  * BMM name: narrative | BMM type: Text
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "narrative")
  private Text narrative;

  /**
  * BMM name: expiry_time | BMM type: Date_time
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "expiry_time")
  private @Nullable RmDateTime expiryTime;

  /**
  * BMM name: activities | BMM type: List<Activity>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "activities")
  private @Nullable List<Activity> activities;

  public Order() {}

  public Order(Text narrative, Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(uid, time, language, subject, archetypeNodeId, name);
    this.narrative = narrative;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Order otherAsOrder = (Order) other;
    return Objects.equals(getOrderTracking(), otherAsOrder.getOrderTracking()) &&
      Objects.equals(getQualifiers(), otherAsOrder.getQualifiers()) &&
      Objects.equals(getGuidelineIds(), otherAsOrder.getGuidelineIds()) &&
      Objects.equals(getUid(), otherAsOrder.getUid()) &&
      Objects.equals(getTime(), otherAsOrder.getTime()) &&
      Objects.equals(getLanguage(), otherAsOrder.getLanguage()) &&
      Objects.equals(getSubject(), otherAsOrder.getSubject()) &&
      Objects.equals(getReporter(), otherAsOrder.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsOrder.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsOrder.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsOrder.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsOrder.getComment()) &&
      Objects.equals(getArchetypeNodeId(), otherAsOrder.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsOrder.getName()) &&
      Objects.equals(getCode(), otherAsOrder.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsOrder.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsOrder.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsOrder.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsOrder.getLinks()) &&
      Objects.equals(narrative, otherAsOrder.narrative) &&
      Objects.equals(expiryTime, otherAsOrder.expiryTime) &&
      Objects.equals(activities, otherAsOrder.activities);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), narrative, expiryTime);
    result = activities == null ? 0 : 31 * result + activities.hashCode();
    return result;
  }

  public Text getNarrative() {
    return narrative;
  }

  public void setNarrative(Text narrative) {
    this.narrative = narrative;
  }

  public @Nullable RmDateTime getExpiryTime() {
    return expiryTime;
  }

  public void setExpiryTime(@Nullable RmDateTime expiryTime) {
    this.expiryTime = expiryTime;
  }

  public @Nullable List<Activity> getActivities() {
    return activities;
  }

  public void setActivities(@Nullable List<Activity> activities) {
    this.activities = activities;
  }

  @Override
  public String bmmClassName() {
    return "Order";
  }

  @Override
  public String toString() {
    return "Order";
  }
}
