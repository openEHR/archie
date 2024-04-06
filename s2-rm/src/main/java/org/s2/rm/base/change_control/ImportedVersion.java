package org.s2.rm.base.change_control;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.identification.ObjectRef;

/**
* BMM name: Imported_version
* BMM generic parameters: Imported_version<T Any>
* BMM ancestors: Version<T>
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Imported_version", propOrder = {
  "item"
})
public class ImportedVersion<T> extends Version<T> {
  /**
  * BMM name: item | BMM type: Original_version<T>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "item")
  private OriginalVersion<T> item;

  public ImportedVersion() {}

  public ImportedVersion(OriginalVersion<T> item, ObjectRef contribution, AuditDetails commitAudit) {
    super(contribution, commitAudit);
    this.item = item;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ImportedVersion<T> otherAsImportedVersion = (ImportedVersion<T>) other;
    return Objects.equals(getContribution(), otherAsImportedVersion.getContribution()) &&
      Objects.equals(getCommitAudit(), otherAsImportedVersion.getCommitAudit()) &&
      Objects.equals(getSignature(), otherAsImportedVersion.getSignature()) &&
      Objects.equals(item, otherAsImportedVersion.item);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), item);
  }

  public OriginalVersion<T> getItem() {
    return item;
  }

  public void setItem(OriginalVersion<T> item) {
    this.item = item;
  }

  @Override
  public String bmmClassName() {
    return "Imported_version";
  }

  @Override
  public String toString() {
    return "Imported_version";
  }
}
