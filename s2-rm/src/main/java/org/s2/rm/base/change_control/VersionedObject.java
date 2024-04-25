package org.s2.rm.base.change_control;


import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Versioned_object
* BMM generic parameters: Versioned_object<T Any>
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Versioned_object", propOrder = {
  "uid",
  "ownerId",
  "timeCreated"
})
public class VersionedObject<T> extends RMObject {
  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "uid")
  private Uuid uid;

  /**
  * BMM name: owner_id | BMM type: Object_ref
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "owner_id")
  private ObjectRef ownerId;

  /**
  * BMM name: time_created | BMM type: Date_time
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "time_created")
  private RmDateTime timeCreated;

  public VersionedObject() {}

  public VersionedObject(Uuid uid, ObjectRef ownerId, RmDateTime timeCreated) {
    this.uid = uid;
    this.ownerId = ownerId;
    this.timeCreated = timeCreated;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    VersionedObject<T> otherAsVersionedObject = (VersionedObject<T>) other;
    return Objects.equals(uid, otherAsVersionedObject.uid) &&
      Objects.equals(ownerId, otherAsVersionedObject.ownerId) &&
      Objects.equals(timeCreated, otherAsVersionedObject.timeCreated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid, ownerId, timeCreated);
  }

  public Uuid getUid() {
    return uid;
  }

  public void setUid(Uuid uid) {
    this.uid = uid;
  }

  public ObjectRef getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(ObjectRef ownerId) {
    this.ownerId = ownerId;
  }

  public RmDateTime getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(RmDateTime timeCreated) {
    this.timeCreated = timeCreated;
  }

  public String bmmClassName() {
    return "Versioned_object";
  }

  @Override
  public String toString() {
    return "Versioned_object";
  }
}
