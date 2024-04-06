package org.s2.rm.base.resource;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Authored_resource
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Authored_resource", propOrder = {
  "originalLanguage",
  "isControlled",
  "translations",
  "description",
  "annotations"
})
public abstract class AuthoredResource {
  /**
  * BMM name: original_language | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "original_language")
  private TerminologyCode originalLanguage;

  /**
  * BMM name: is_controlled | BMM type: Boolean
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "is_controlled")
  private boolean isControlled;

  /**
  * BMM name: translations | BMM type: Hash<String,Translation_details>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "translations")
  private @Nullable Map<String, TranslationDetails> translations;

  /**
  * BMM name: description | BMM type: Resource_description
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "description")
  private @Nullable ResourceDescription description;

  /**
  * BMM name: annotations | BMM type: Resource_annotations
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "annotations")
  private @Nullable ResourceAnnotations annotations;

  public AuthoredResource() {}

  public AuthoredResource(TerminologyCode originalLanguage) {
    this.originalLanguage = originalLanguage;
  }

  public TerminologyCode getOriginalLanguage() {
    return originalLanguage;
  }

  public void setOriginalLanguage(TerminologyCode originalLanguage) {
    this.originalLanguage = originalLanguage;
  }

  public boolean getIsControlled() {
    return isControlled;
  }

  public void setIsControlled(boolean isControlled) {
    this.isControlled = isControlled;
  }

  public @Nullable Map<String, TranslationDetails> getTranslations() {
    return translations;
  }

  public void setTranslations(@Nullable Map<String, TranslationDetails> translations) {
    this.translations = translations;
  }

  public @Nullable ResourceDescription getDescription() {
    return description;
  }

  public void setDescription(@Nullable ResourceDescription description) {
    this.description = description;
  }

  public @Nullable ResourceAnnotations getAnnotations() {
    return annotations;
  }

  public void setAnnotations(@Nullable ResourceAnnotations annotations) {
    this.annotations = annotations;
  }

  public String bmmClassName() {
    return "Authored_resource";
  }

  @Override
  public String toString() {
    return "Authored_resource";
  }
}
