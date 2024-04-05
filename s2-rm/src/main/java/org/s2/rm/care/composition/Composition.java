package main.java.org.s2.rm.care.composition;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.participation.PartyProxy;

/**
* BMM name: Composition
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Composition", propOrder = {
  "uid",
  "language",
  "territory",
  "category",
  "composer",
  "context",
  "content"
})
public class Composition extends Locatable {
  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 1..1
  */
  @XmlElement(name = "uid")
  private @Nonnull Uuid uid;

  /**
  * BMM name: language | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "language")
  private @Nonnull TerminologyCode language;

  /**
  * BMM name: territory | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "territory")
  private @Nonnull TerminologyCode territory;

  /**
  * BMM name: category | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "category")
  private @Nonnull TerminologyTerm category;

  /**
  * BMM name: composer | BMM type: Party_proxy
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "composer")
  private @Nonnull PartyProxy composer;

  /**
  * BMM name: context | BMM type: Event_context
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "context")
  private @Nullable EventContext context;

  /**
  * BMM name: content | BMM type: List<Content_item>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "content")
  private @Nullable List<ContentItem> content;

  public Composition() {}

  public Composition(@Nonnull Uuid uid, @Nonnull TerminologyCode language, @Nonnull TerminologyCode territory, @Nonnull TerminologyTerm category, @Nonnull PartyProxy composer, @Nonnull String archetypeNodeId, @Nonnull String name) {
    super(archetypeNodeId, name);
    this.uid = uid;
    this.language = language;
    this.territory = territory;
    this.category = category;
    this.composer = composer;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Composition otherAsComposition = (Composition) other;
    return Objects.equals(uid, otherAsComposition.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsComposition.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsComposition.getName()) &&
      Objects.equals(getCode(), otherAsComposition.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsComposition.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsComposition.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsComposition.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsComposition.getLinks()) &&
      Objects.equals(language, otherAsComposition.language) &&
      Objects.equals(territory, otherAsComposition.territory) &&
      Objects.equals(category, otherAsComposition.category) &&
      Objects.equals(composer, otherAsComposition.composer) &&
      Objects.equals(context, otherAsComposition.context) &&
      Objects.equals(content, otherAsComposition.content);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, language, territory, category, composer, context);
    result = content == null ? 0 : 31 * result + content.hashCode();
    return result;
  }

  public @Nonnull Uuid getUid() {
    return uid;
  }

  public void setUid(@Nonnull Uuid uid) {
    this.uid = uid;
  }

  public @Nonnull TerminologyCode getLanguage() {
    return language;
  }

  public void setLanguage(@Nonnull TerminologyCode language) {
    this.language = language;
  }

  public @Nonnull TerminologyCode getTerritory() {
    return territory;
  }

  public void setTerritory(@Nonnull TerminologyCode territory) {
    this.territory = territory;
  }

  public @Nonnull TerminologyTerm getCategory() {
    return category;
  }

  public void setCategory(@Nonnull TerminologyTerm category) {
    this.category = category;
  }

  public @Nonnull PartyProxy getComposer() {
    return composer;
  }

  public void setComposer(@Nonnull PartyProxy composer) {
    this.composer = composer;
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
