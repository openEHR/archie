package main.java.org.s2.rm.care.ehr;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.identification.InternetId;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Ehr
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ehr", propOrder = {
  "systemId",
  "ehrId",
  "timeCreated",
  "ehrAccess",
  "ehrStatus",
  "directory",
  "folders",
  "compositions",
  "contributions"
})
public class Ehr {
  /**
  * BMM name: system_id | BMM type: Internet_id
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "system_id")
  private @Nonnull InternetId systemId;

  /**
  * BMM name: ehr_id | BMM type: Uuid
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "ehr_id")
  private @Nonnull Uuid ehrId;

  /**
  * BMM name: time_created | BMM type: Date_time
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "time_created")
  private @Nonnull RmDateTime timeCreated;

  /**
  * BMM name: ehr_access | BMM type: Object_ref
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "ehr_access")
  private @Nonnull ObjectRef ehrAccess;

  /**
  * BMM name: ehr_status | BMM type: Object_ref
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "ehr_status")
  private @Nonnull ObjectRef ehrStatus;

  /**
  * BMM name: directory | BMM type: Object_ref
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "directory")
  private @Nullable ObjectRef directory;

  /**
  * BMM name: folders | BMM type: List<Object_ref>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "folders")
  private @Nullable List<ObjectRef> folders;

  /**
  * BMM name: compositions | BMM type: List<Object_ref>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "compositions")
  private @Nullable List<ObjectRef> compositions;

  /**
  * BMM name: contributions | BMM type: List<Object_ref>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "contributions")
  private @Nonnull List<ObjectRef> contributions;

  public Ehr() {}

  public Ehr(@Nonnull InternetId systemId, @Nonnull Uuid ehrId, @Nonnull RmDateTime timeCreated, @Nonnull ObjectRef ehrAccess, @Nonnull ObjectRef ehrStatus, @Nonnull List<ObjectRef> contributions) {
    this.systemId = systemId;
    this.ehrId = ehrId;
    this.timeCreated = timeCreated;
    this.ehrAccess = ehrAccess;
    this.ehrStatus = ehrStatus;
    this.contributions = contributions;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Ehr otherAsEhr = (Ehr) other;
    return Objects.equals(systemId, otherAsEhr.systemId) &&
      Objects.equals(ehrId, otherAsEhr.ehrId) &&
      Objects.equals(timeCreated, otherAsEhr.timeCreated) &&
      Objects.equals(ehrAccess, otherAsEhr.ehrAccess) &&
      Objects.equals(ehrStatus, otherAsEhr.ehrStatus) &&
      Objects.equals(directory, otherAsEhr.directory) &&
      Objects.equals(folders, otherAsEhr.folders) &&
      Objects.equals(compositions, otherAsEhr.compositions) &&
      Objects.equals(contributions, otherAsEhr.contributions);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), systemId, ehrId, timeCreated, ehrAccess, ehrStatus, directory);
    result = folders == null ? 0 : 31 * result + folders.hashCode();
    result = compositions == null ? 0 : 31 * result + compositions.hashCode();
    result = contributions == null ? 0 : 31 * result + contributions.hashCode();
    return result;
  }

  public @Nonnull InternetId getSystemId() {
    return systemId;
  }

  public void setSystemId(@Nonnull InternetId systemId) {
    this.systemId = systemId;
  }

  public @Nonnull Uuid getEhrId() {
    return ehrId;
  }

  public void setEhrId(@Nonnull Uuid ehrId) {
    this.ehrId = ehrId;
  }

  public @Nonnull RmDateTime getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(@Nonnull RmDateTime timeCreated) {
    this.timeCreated = timeCreated;
  }

  public @Nonnull ObjectRef getEhrAccess() {
    return ehrAccess;
  }

  public void setEhrAccess(@Nonnull ObjectRef ehrAccess) {
    this.ehrAccess = ehrAccess;
  }

  public @Nonnull ObjectRef getEhrStatus() {
    return ehrStatus;
  }

  public void setEhrStatus(@Nonnull ObjectRef ehrStatus) {
    this.ehrStatus = ehrStatus;
  }

  public @Nullable ObjectRef getDirectory() {
    return directory;
  }

  public void setDirectory(@Nullable ObjectRef directory) {
    this.directory = directory;
  }

  public @Nullable List<ObjectRef> getFolders() {
    return folders;
  }

  public void setFolders(@Nullable List<ObjectRef> folders) {
    this.folders = folders;
  }

  public @Nullable List<ObjectRef> getCompositions() {
    return compositions;
  }

  public void setCompositions(@Nullable List<ObjectRef> compositions) {
    this.compositions = compositions;
  }

  public @Nonnull List<ObjectRef> getContributions() {
    return contributions;
  }

  public void setContributions(@Nonnull List<ObjectRef> contributions) {
    this.contributions = contributions;
  }

  public String bmmClassName() {
    return "Ehr";
  }

  @Override
  public String toString() {
    return "Ehr";
  }
}
