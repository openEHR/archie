package org.s2.rm.care.ehr;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Folder
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Folder", propOrder = {
  "uid",
  "folders",
  "items",
  "details"
})
public class Folder extends Locatable {
  /**
  * BMM name: folders | BMM type: List<Folder>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "folders")
  private @Nullable List<Folder> folders;

  /**
  * BMM name: items | BMM type: List<Object_ref>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "items")
  private @Nullable List<ObjectRef> items;

  /**
  * BMM name: details | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "details")
  private @Nullable List<Node> details;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Folder() {}

  public Folder(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Folder otherAsFolder = (Folder) other;
    return Objects.equals(uid, otherAsFolder.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsFolder.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsFolder.getName()) &&
      Objects.equals(getCode(), otherAsFolder.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsFolder.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsFolder.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsFolder.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsFolder.getLinks()) &&
      Objects.equals(folders, otherAsFolder.folders) &&
      Objects.equals(items, otherAsFolder.items) &&
      Objects.equals(details, otherAsFolder.details);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid);
    result = folders == null ? 0 : 31 * result + folders.hashCode();
    result = items == null ? 0 : 31 * result + items.hashCode();
    result = details == null ? 0 : 31 * result + details.hashCode();
    return result;
  }

  public @Nullable List<Folder> getFolders() {
    return folders;
  }

  public void setFolders(@Nullable List<Folder> folders) {
    this.folders = folders;
  }

  public @Nullable List<ObjectRef> getItems() {
    return items;
  }

  public void setItems(@Nullable List<ObjectRef> items) {
    this.items = items;
  }

  public @Nullable List<Node> getDetails() {
    return details;
  }

  public void setDetails(@Nullable List<Node> details) {
    this.details = details;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Folder";
  }

  @Override
  public String toString() {
    return "Folder";
  }
}
