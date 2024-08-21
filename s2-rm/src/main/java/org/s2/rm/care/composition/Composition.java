package org.s2.rm.care.composition;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.InfoItem;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Composition
* BMM ancestors: Info_item
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Composition", propOrder = {
  "uid",
  "language",
  "territory",
  "category",
  "author",
  "context",
  "content"
})
public class Composition extends InfoItem {
  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "uid")
  private Uuid uid;

  /**
  * BMM name: language | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  * valueConstraint: iso_639-1
  */
  @XmlElement(name = "language")
  private TerminologyCode language;

  /**
  * BMM name: territory | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  * valueConstraint: iso_3166-1-alpha2
  */
  @XmlElement(name = "territory")
  private TerminologyCode territory;

  /**
  * BMM name: category | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  * valueConstraint: s2.CompositionCategory
  */
  @XmlElement(name = "category")
  private TerminologyTerm category;

  /**
  * BMM name: author | BMM type: Party_proxy
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "author")
  private PartyProxy author;

  /**
  * BMM name: context | BMM type: Event_context
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "context")
  private @Nullable EventContext context;

  /**
  * BMM name: content | BMM type: {@code List<Content_item>}
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "content")
  private @Nullable List<ContentItem> content;

  public Composition() {}

  public Composition(Uuid uid, TerminologyCode language, TerminologyCode territory, TerminologyTerm category, PartyProxy author, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.uid = uid;
    this.language = language;
    this.territory = territory;
    this.category = category;
    this.author = author;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Composition otherAsComposition = (Composition) other;
    return Objects.equals(getCode(), otherAsComposition.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsComposition.getOriginalCode()) &&
      Objects.equals(getLinks(), otherAsComposition.getLinks()) &&
      Objects.equals(uid, otherAsComposition.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsComposition.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsComposition.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsComposition.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsComposition.getFeederAudit()) &&
      Objects.equals(language, otherAsComposition.language) &&
      Objects.equals(territory, otherAsComposition.territory) &&
      Objects.equals(category, otherAsComposition.category) &&
      Objects.equals(author, otherAsComposition.author) &&
      Objects.equals(context, otherAsComposition.context) &&
      Objects.equals(content, otherAsComposition.content);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, language, territory, category, author, context);
    result = content == null ? 0 : 31 * result + content.hashCode();
    return result;
  }

  public Uuid getUid() {
    return uid;
  }

  public void setUid(Uuid uid) {
    this.uid = uid;
  }

  public TerminologyCode getLanguage() {
    return language;
  }

  public void setLanguage(TerminologyCode language) {
    this.language = language;
  }

  public TerminologyCode getTerritory() {
    return territory;
  }

  public void setTerritory(TerminologyCode territory) {
    this.territory = territory;
  }

  public TerminologyTerm getCategory() {
    return category;
  }

  public void setCategory(TerminologyTerm category) {
    this.category = category;
  }

  public PartyProxy getAuthor() {
    return author;
  }

  public void setAuthor(PartyProxy author) {
    this.author = author;
  }

  public @Nullable EventContext getContext() {
    return context;
  }

  public void setContext(@Nullable EventContext context) {
    this.context = context;
  }

  public @Nullable List<ContentItem> getContent() {
    return content;
  }

  public void setContent(@Nullable List<ContentItem> content) {
    this.content = content;
  }

  @Override
  public String bmmClassName() {
    return "Composition";
  }

  @Override
  public String toString() {
    return "Composition";
  }
}
