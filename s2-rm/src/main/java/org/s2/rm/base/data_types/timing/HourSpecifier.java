package org.s2.rm.base.data_types.timing;

import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.foundation_types.time.RmDuration;

/**
* BMM name: Hour_specifier
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Hour_specifier", propOrder = {
  "duration"
})
public abstract class HourSpecifier extends RMObject {
  /**
  * BMM name: duration | BMM type: Duration
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "duration")
  private @Nullable RmDuration duration;


  public @Nullable RmDuration getDuration() {
    return duration;
  }

  public void setDuration(@Nullable RmDuration duration) {
    this.duration = duration;
  }

  public String bmmClassName() {
    return "Hour_specifier";
  }

  @Override
  public String toString() {
    return "Hour_specifier";
  }
}
