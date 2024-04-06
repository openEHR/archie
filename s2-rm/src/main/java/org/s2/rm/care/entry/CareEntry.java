package org.s2.rm.care.entry;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.primitive_types.Uri;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* Abstract Entry subtype corresponding to any type of Entry in the clinical care cycle.
* BMM name: Care_entry
* BMM ancestors: Entry
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Care_entry", propOrder = {
  "qualifiers",
  "guidelineIds"
})
public abstract class CareEntry extends Entry {
  /**
  * BMM name: qualifiers | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "qualifiers")
  private @Nullable List<Node> qualifiers;

  /**
  * BMM name: guideline_ids | BMM type: List<Uri>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "guideline_ids")
  private @Nullable List<Uri> guidelineIds;

  public CareEntry() {}

  public CareEntry(Uuid uid, RmDateTime time, TerminologyCode language, PartyProxy subject, String archetypeNodeId, String name) {
    super(uid, time, language, subject, archetypeNodeId, name);
  }

  public @Nullable List<Node> getQualifiers() {
    return qualifiers;
  }

  public void setQualifiers(@Nullable List<Node> qualifiers) {
    this.qualifiers = qualifiers;
  }

  public @Nullable List<Uri> getGuidelineIds() {
    return guidelineIds;
  }

  public void setGuidelineIds(@Nullable List<Uri> guidelineIds) {
    this.guidelineIds = guidelineIds;
  }

  @Override
  public String bmmClassName() {
    return "Care_entry";
  }

  @Override
  public String toString() {
    return "Care_entry";
  }
}
