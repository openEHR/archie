package org.s2.rm.care.entry;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.timing.Timing;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Activity
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Activity", propOrder = {
  "uid",
  "description",
  "timing"
})
public class Activity extends Locatable {
  /**
  * BMM name: description | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "description")
  private @Nullable List<Node> description;

  /**
  * BMM name: timing | BMM type: Timing
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "timing")
  private @Nullable Timing timing;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Activity() {}

  public Activity(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Activity otherAsActivity = (Activity) other;
    return Objects.equals(uid, otherAsActivity.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsActivity.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsActivity.getName()) &&
      Objects.equals(getCode(), otherAsActivity.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsActivity.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsActivity.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsActivity.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsActivity.getLinks()) &&
      Objects.equals(description, otherAsActivity.description) &&
      Objects.equals(timing, otherAsActivity.timing);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, timing);
    result = description == null ? 0 : 31 * result + description.hashCode();
    return result;
  }

  public @Nullable List<Node> getDescription() {
    return description;
  }

  public void setDescription(@Nullable List<Node> description) {
    this.description = description;
  }

  public @Nullable Timing getTiming() {
    return timing;
  }

  public void setTiming(@Nullable Timing timing) {
    this.timing = timing;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Activity";
  }

  @Override
  public String toString() {
    return "Activity";
  }
}
