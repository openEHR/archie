package org.s2.rm.entity.resource;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.DateTime;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Service_use
* BMM ancestors: Resource_use
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Service_use", propOrder = {
  "uid"
})
public class ServiceUse extends ResourceUse {

  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public ServiceUse() {}

  public ServiceUse(DateTime startTime, String archetypeNodeId, String name) {
    super(startTime, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ServiceUse otherAsServiceUse = (ServiceUse) other;
    return Objects.equals(getStartTime(), otherAsServiceUse.getStartTime()) &&
      Objects.equals(getDuration(), otherAsServiceUse.getDuration()) &&
      Objects.equals(getCostData(), otherAsServiceUse.getCostData()) &&
      Objects.equals(getDescription(), otherAsServiceUse.getDescription()) &&
      Objects.equals(uid, otherAsServiceUse.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsServiceUse.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsServiceUse.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsServiceUse.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsServiceUse.getFeederAudit());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid);
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Service_use";
  }

  @Override
  public String toString() {
    return "Service_use";
  }
}
