package org.s2.rm.base.patterns.data_structures;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.foundation_types.time.RmDuration;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.definitions.SampleFunctionKind;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Interval_event
* BMM ancestors: Event
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Interval_event", propOrder = {
  "uid",
  "width",
  "sampleCount",
  "sampleFunction"
})
public class IntervalEvent extends Event {
  /**
  * BMM name: width | BMM type: Duration
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "width")
  private RmDuration width;

  /**
  * BMM name: sample_count | BMM type: Integer
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "sample_count")
  private int sampleCount;

  /**
  * BMM name: sample_function | BMM type: Sample_function_kind
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "sample_function")
  private @Nullable SampleFunctionKind sampleFunction;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public IntervalEvent() {}

  public IntervalEvent(RmDuration width, RmDateTime time, String archetypeNodeId, String name) {
    super(time, archetypeNodeId, name);
    this.width = width;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    IntervalEvent otherAsIntervalEvent = (IntervalEvent) other;
    return Objects.equals(getTime(), otherAsIntervalEvent.getTime()) &&
      Objects.equals(getItems(), otherAsIntervalEvent.getItems()) &&
      Objects.equals(uid, otherAsIntervalEvent.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsIntervalEvent.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsIntervalEvent.getName()) &&
      Objects.equals(getCode(), otherAsIntervalEvent.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsIntervalEvent.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsIntervalEvent.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsIntervalEvent.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsIntervalEvent.getLinks()) &&
      Objects.equals(width, otherAsIntervalEvent.width) &&
      Objects.equals(sampleCount, otherAsIntervalEvent.sampleCount) &&
      Objects.equals(sampleFunction, otherAsIntervalEvent.sampleFunction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid, width, sampleCount, sampleFunction);
  }

  public RmDuration getWidth() {
    return width;
  }

  public void setWidth(RmDuration width) {
    this.width = width;
  }

  public int getSampleCount() {
    return sampleCount;
  }

  public void setSampleCount(int sampleCount) {
    this.sampleCount = sampleCount;
  }

  public @Nullable SampleFunctionKind getSampleFunction() {
    return sampleFunction;
  }

  public void setSampleFunction(@Nullable SampleFunctionKind sampleFunction) {
    this.sampleFunction = sampleFunction;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Interval_event";
  }

  @Override
  public String toString() {
    return "Interval_event";
  }
}
