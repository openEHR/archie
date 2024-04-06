package org.s2.rm.care.ehr;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.change_control.VersionedObject;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Versioned_folder
* BMM ancestors: Versioned_object<Folder>
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Versioned_folder")
public class VersionedFolder<T extends Folder> extends VersionedObject<Folder> {
  public VersionedFolder() {}

  public VersionedFolder(Uuid uid, ObjectRef ownerId, RmDateTime timeCreated) {
    super(uid, ownerId, timeCreated);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    VersionedFolder<T> otherAsVersionedFolder = (VersionedFolder<T>) other;
    return Objects.equals(getUid(), otherAsVersionedFolder.getUid()) &&
      Objects.equals(getOwnerId(), otherAsVersionedFolder.getOwnerId()) &&
      Objects.equals(getTimeCreated(), otherAsVersionedFolder.getTimeCreated());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Versioned_folder";
  }

  @Override
  public String toString() {
    return "Versioned_folder";
  }
}
