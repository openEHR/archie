package org.s2.rm.care.ehr;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.change_control.VersionedObject;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Versioned_ehr_access
* BMM ancestors: Versioned_object<Folder>
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Versioned_ehr_access")
public class VersionedEhrAccess<T extends Folder> extends VersionedObject<Folder> {
  public VersionedEhrAccess() {}

  public VersionedEhrAccess(Uuid uid, ObjectRef ownerId, RmDateTime timeCreated) {
    super(uid, ownerId, timeCreated);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    VersionedEhrAccess<T> otherAsVersionedEhrAccess = (VersionedEhrAccess<T>) other;
    return Objects.equals(getUid(), otherAsVersionedEhrAccess.getUid()) &&
      Objects.equals(getOwnerId(), otherAsVersionedEhrAccess.getOwnerId()) &&
      Objects.equals(getTimeCreated(), otherAsVersionedEhrAccess.getTimeCreated());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Versioned_ehr_access";
  }

  @Override
  public String toString() {
    return "Versioned_ehr_access";
  }
}
