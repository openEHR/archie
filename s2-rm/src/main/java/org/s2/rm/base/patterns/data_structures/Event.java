package org.s2.rm.base.patterns.data_structures;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.archetyped.Locatable;

/**
* BMM name: Event
* BMM ancestors: Locatable
* isAbstract: true | isPrimitiveType: false | isOverride: false
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
  private RmDateTime time;

  /**
  * BMM name: items | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "items")
  private @Nullable List<Node> items;

  public Event() {}

  public Event(RmDateTime time, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.time = time;
  }

  public RmDateTime getTime() {
    return time;
  }

  public void setTime(RmDateTime time) {
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
