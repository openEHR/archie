package org.s2.rm.care.ehr;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.change_control.VersionedObject;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Versioned_composition
* BMM ancestors: Versioned_object<Folder>
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Versioned_composition")
public class VersionedComposition<T extends Folder> extends VersionedObject<Folder> {
  public VersionedComposition() {}

  public VersionedComposition(Uuid uid, ObjectRef ownerId, RmDateTime timeCreated) {
    super(uid, ownerId, timeCreated);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    VersionedComposition<T> otherAsVersionedComposition = (VersionedComposition<T>) other;
    return Objects.equals(getUid(), otherAsVersionedComposition.getUid()) &&
      Objects.equals(getOwnerId(), otherAsVersionedComposition.getOwnerId()) &&
      Objects.equals(getTimeCreated(), otherAsVersionedComposition.getTimeCreated());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Versioned_composition";
  }

  @Override
  public String toString() {
    return "Versioned_composition";
  }
}
