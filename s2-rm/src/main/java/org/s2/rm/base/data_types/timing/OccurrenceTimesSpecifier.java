package org.s2.rm.base.data_types.timing;

import javax.xml.bind.annotation.*;

/**
* BMM name: Occurrence_times_specifier
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Occurrence_times_specifier", propOrder = {
  "eventCount",
  "eventCountUpper"
})
public abstract class OccurrenceTimesSpecifier {
  /**
  * BMM name: event_count | BMM type: Integer
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "event_count")
  private int eventCount;

  /**
  * BMM name: event_count_upper | BMM type: Integer
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "event_count_upper")
  private int eventCountUpper;

  public OccurrenceTimesSpecifier() {}


  public int getEventCount() {
    return eventCount;
  }

  public void setEventCount(int eventCount) {
    this.eventCount = eventCount;
  }

  public int getEventCountUpper() {
    return eventCountUpper;
  }

  public void setEventCountUpper(int eventCountUpper) {
    this.eventCountUpper = eventCountUpper;
  }

  public String bmmClassName() {
    return "Occurrence_times_specifier";
  }

  @Override
  public String toString() {
    return "Occurrence_times_specifier";
  }
}
