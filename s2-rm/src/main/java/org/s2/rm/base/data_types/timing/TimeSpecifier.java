package org.s2.rm.base.data_types.timing;

import com.nedap.archie.base.RMObject;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Time_specifier
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Time_specifier", propOrder = {
  "eventCount"
})
public abstract class TimeSpecifier extends RMObject {
  /**
  * BMM name: event_count | BMM type: Integer
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "event_count")
  private int eventCount;

  public TimeSpecifier() {}

  public TimeSpecifier(int eventCount) {
    this.eventCount = eventCount;
  }

  public int getEventCount() {
    return eventCount;
  }

  public void setEventCount(int eventCount) {
    this.eventCount = eventCount;
  }

  public String bmmClassName() {
    return "Time_specifier";
  }

  @Override
  public String toString() {
    return "Time_specifier";
  }
}
