package org.s2.rm.base.model_support.archetyped;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Info_item
* BMM ancestors: Locatable
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Info_item", propOrder = {
  "code",
  "originalCode",
  "links"
})
public abstract class InfoItem extends Locatable {
  /**
  * BMM name: code | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "code")
  private @Nullable TerminologyTerm code;

  /**
  * BMM name: original_code | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "original_code")
  private @Nullable TerminologyTerm originalCode;

  /**
  * BMM name: links | BMM type: List<{@literal Link}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "links")
  private @Nullable List<Link> links;

  public InfoItem() {}

  public InfoItem(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  public @Nullable TerminologyTerm getCode() {
    return code;
  }

  public void setCode(@Nullable TerminologyTerm code) {
    this.code = code;
  }

  public @Nullable TerminologyTerm getOriginalCode() {
    return originalCode;
  }

  public void setOriginalCode(@Nullable TerminologyTerm originalCode) {
    this.originalCode = originalCode;
  }

  public @Nullable List<Link> getLinks() {
    return links;
  }

  public void setLinks(@Nullable List<Link> links) {
    this.links = links;
  }

  @Override
  public String bmmClassName() {
    return "Info_item";
  }

  @Override
  public String toString() {
    return "Info_item";
  }
}
