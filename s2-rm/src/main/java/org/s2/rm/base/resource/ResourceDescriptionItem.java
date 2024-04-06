package org.s2.rm.base.resource;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Resource_description_item
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resource_description_item", propOrder = {
  "language",
  "purpose",
  "keywords",
  "use",
  "misuse",
  "originalResourceUri",
  "otherDetails"
})
public class ResourceDescriptionItem {
  /**
  * BMM name: language | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "language")
  private TerminologyCode language;

  /**
  * BMM name: purpose | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "purpose")
  private String purpose;

  /**
  * BMM name: keywords | BMM type: List<String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "keywords")
  private @Nullable List<String> keywords;

  /**
  * BMM name: use | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "use")
  private @Nullable String use;

  /**
  * BMM name: misuse | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "misuse")
  private @Nullable String misuse;

  /**
  * BMM name: original_resource_uri | BMM type: List<Hash<String,String>>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "original_resource_uri")
  private @Nullable List<Map<String, String>> originalResourceUri;

  /**
  * BMM name: other_details | BMM type: Hash<String,String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "other_details")
  private @Nullable Map<String, String> otherDetails;

  public ResourceDescriptionItem() {}

  public ResourceDescriptionItem(TerminologyCode language, String purpose) {
    this.language = language;
    this.purpose = purpose;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ResourceDescriptionItem otherAsResourceDescriptionItem = (ResourceDescriptionItem) other;
    return Objects.equals(language, otherAsResourceDescriptionItem.language) &&
      Objects.equals(purpose, otherAsResourceDescriptionItem.purpose) &&
      Objects.equals(keywords, otherAsResourceDescriptionItem.keywords) &&
      Objects.equals(use, otherAsResourceDescriptionItem.use) &&
      Objects.equals(misuse, otherAsResourceDescriptionItem.misuse) &&
      Objects.equals(originalResourceUri, otherAsResourceDescriptionItem.originalResourceUri) &&
      Objects.equals(otherDetails, otherAsResourceDescriptionItem.otherDetails);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), language, purpose, use, misuse, otherDetails);
    result = keywords == null ? 0 : 31 * result + keywords.hashCode();
    result = originalResourceUri == null ? 0 : 31 * result + originalResourceUri.hashCode();
    return result;
  }

  public TerminologyCode getLanguage() {
    return language;
  }

  public void setLanguage(TerminologyCode language) {
    this.language = language;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public @Nullable List<String> getKeywords() {
    return keywords;
  }

  public void setKeywords(@Nullable List<String> keywords) {
    this.keywords = keywords;
  }

  public @Nullable String getUse() {
    return use;
  }

  public void setUse(@Nullable String use) {
    this.use = use;
  }

  public @Nullable String getMisuse() {
    return misuse;
  }

  public void setMisuse(@Nullable String misuse) {
    this.misuse = misuse;
  }

  public @Nullable List<Map<String, String>> getOriginalResourceUri() {
    return originalResourceUri;
  }

  public void setOriginalResourceUri(@Nullable List<Map<String, String>> originalResourceUri) {
    this.originalResourceUri = originalResourceUri;
  }

  public @Nullable Map<String, String> getOtherDetails() {
    return otherDetails;
  }

  public void setOtherDetails(@Nullable Map<String, String> otherDetails) {
    this.otherDetails = otherDetails;
  }

  public String bmmClassName() {
    return "Resource_description_item";
  }

  @Override
  public String toString() {
    return "Resource_description_item";
  }
}
