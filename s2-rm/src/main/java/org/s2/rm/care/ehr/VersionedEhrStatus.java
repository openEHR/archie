package org.s2.rm.care.ehr;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.change_control.VersionedObject;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Versioned_ehr_status
* BMM ancestors: Versioned_object<Folder>
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Versioned_ehr_status")
public class VersionedEhrStatus<T extends Folder> extends VersionedObject<Folder> {
  public VersionedEhrStatus() {}

  public VersionedEhrStatus(Uuid uid, ObjectRef ownerId, RmDateTime timeCreated) {
    super(uid, ownerId, timeCreated);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    VersionedEhrStatus<T> otherAsVersionedEhrStatus = (VersionedEhrStatus<T>) other;
    return Objects.equals(getUid(), otherAsVersionedEhrStatus.getUid()) &&
      Objects.equals(getOwnerId(), otherAsVersionedEhrStatus.getOwnerId()) &&
      Objects.equals(getTimeCreated(), otherAsVersionedEhrStatus.getTimeCreated());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Versioned_ehr_status";
  }

  @Override
  public String toString() {
    return "Versioned_ehr_status";
  }
}
