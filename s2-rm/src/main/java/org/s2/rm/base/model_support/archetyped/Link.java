package org.s2.rm.base.model_support.archetyped;


import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.foundation_types.primitive_types.Uri;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Link
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Link", propOrder = {
  "meaning",
  "type",
  "target"
})
public class Link extends RMObject {
  /**
  * BMM name: meaning | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "meaning")
  private TerminologyTerm meaning;

  /**
  * BMM name: type | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "type")
  private TerminologyTerm type;

  /**
  * BMM name: target | BMM type: Uri
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "target")
  private Uri target;

  public Link() {}

  public Link(TerminologyTerm meaning, TerminologyTerm type, Uri target) {
    this.meaning = meaning;
    this.type = type;
    this.target = target;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Link otherAsLink = (Link) other;
    return Objects.equals(meaning, otherAsLink.meaning) &&
      Objects.equals(type, otherAsLink.type) &&
      Objects.equals(target, otherAsLink.target);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), meaning, type, target);
  }

  public TerminologyTerm getMeaning() {
    return meaning;
  }

  public void setMeaning(TerminologyTerm meaning) {
    this.meaning = meaning;
  }

  public TerminologyTerm getType() {
    return type;
  }

  public void setType(TerminologyTerm type) {
    this.type = type;
  }

  public Uri getTarget() {
    return target;
  }

  public void setTarget(Uri target) {
    this.target = target;
  }

  public String bmmClassName() {
    return "Link";
  }

  @Override
  public String toString() {
    return "Link";
  }
}
