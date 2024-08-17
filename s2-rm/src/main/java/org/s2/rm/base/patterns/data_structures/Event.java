package org.s2.rm.base.patterns.data_structures;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.DateTime;
import org.s2.rm.base.model_support.archetyped.Locatable;

/**
* BMM name: Event
* BMM ancestors: Locatable
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Event", propOrder = {
  "time",
  "items"
})
public abstract class Event extends Locatable {
  /**
  * BMM name: time | BMM type: Date_time
  * isMandatory: true | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "time")
  private DateTime time;

  /**
  * BMM name: items | BMM type: List<{@literal Node}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "items")
  private @Nullable List<Node> items;

  public Event() {}

  public Event(DateTime time, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.time = time;
  }

  public DateTime getTime() {
    return time;
  }

  public void setTime(DateTime time) {
    this.time = time;
  }

  public @Nullable List<Node> getItems() {
    return items;
  }

  public void setItems(@Nullable List<Node> items) {
    this.items = items;
  }

  @Override
  public String bmmClassName() {
    return "Event";
  }

  @Override
  public String toString() {
    return "Event";
  }
}
