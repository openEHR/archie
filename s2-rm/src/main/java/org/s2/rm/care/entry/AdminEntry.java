package org.s2.rm.care.entry;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.time.DateTime;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Admin_entry
* BMM ancestors: Entry
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Admin_entry", propOrder = {
  "data"
})
public class AdminEntry extends Entry {
  /**
  * BMM name: data | BMM type: List<{@literal Node}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "data")
  private @Nullable List<Node> data;

  public AdminEntry() {}

  public AdminEntry(Uuid uid, DateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(uid, time, language, subject, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    AdminEntry otherAsAdminEntry = (AdminEntry) other;
    return Objects.equals(getUid(), otherAsAdminEntry.getUid()) &&
      Objects.equals(getTime(), otherAsAdminEntry.getTime()) &&
      Objects.equals(getLanguage(), otherAsAdminEntry.getLanguage()) &&
      Objects.equals(getSubject(), otherAsAdminEntry.getSubject()) &&
      Objects.equals(getReporter(), otherAsAdminEntry.getReporter()) &&
      Objects.equals(getAuthorizationActions(), otherAsAdminEntry.getAuthorizationActions()) &&
      Objects.equals(getOtherParticipations(), otherAsAdminEntry.getOtherParticipations()) &&
      Objects.equals(getWorkflowId(), otherAsAdminEntry.getWorkflowId()) &&
      Objects.equals(getComment(), otherAsAdminEntry.getComment()) &&
      Objects.equals(getCode(), otherAsAdminEntry.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsAdminEntry.getOriginalCode()) &&
      Objects.equals(getLinks(), otherAsAdminEntry.getLinks()) &&
      Objects.equals(getArchetypeNodeId(), otherAsAdminEntry.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsAdminEntry.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsAdminEntry.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsAdminEntry.getFeederAudit()) &&
      Objects.equals(data, otherAsAdminEntry.data);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode());
    result = data == null ? 0 : 31 * result + data.hashCode();
    return result;
  }

  public @Nullable List<Node> getData() {
    return data;
  }

  public void setData(@Nullable List<Node> data) {
    this.data = data;
  }

  @Override
  public String bmmClassName() {
    return "Admin_entry";
  }

  @Override
  public String toString() {
    return "Admin_entry";
  }
}
