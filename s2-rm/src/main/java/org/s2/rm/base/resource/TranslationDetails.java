package org.s2.rm.base.resource;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Translation_details
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Translation_details", propOrder = {
  "language",
  "author",
  "accreditation",
  "versionLastTranslated",
  "otherDetails",
  "otherContributors"
})
public class TranslationDetails {
  /**
  * BMM name: language | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "language")
  private TerminologyCode language;

  /**
  * BMM name: author | BMM type: Hash<String,String>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "author")
  private Map<String, String> author;

  /**
  * BMM name: accreditation | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "accreditation")
  private @Nullable String accreditation;

  /**
  * BMM name: version_last_translated | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "version_last_translated")
  private @Nullable String versionLastTranslated;

  /**
  * BMM name: other_details | BMM type: Hash<String,String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "other_details")
  private @Nullable Map<String, String> otherDetails;

  /**
  * BMM name: other_contributors | BMM type: List<String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "other_contributors")
  private @Nullable List<String> otherContributors;

  public TranslationDetails() {}

  public TranslationDetails(TerminologyCode language, Map<String, String> author) {
    this.language = language;
    this.author = author;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    TranslationDetails otherAsTranslationDetails = (TranslationDetails) other;
    return Objects.equals(language, otherAsTranslationDetails.language) &&
      Objects.equals(author, otherAsTranslationDetails.author) &&
      Objects.equals(accreditation, otherAsTranslationDetails.accreditation) &&
      Objects.equals(versionLastTranslated, otherAsTranslationDetails.versionLastTranslated) &&
      Objects.equals(otherDetails, otherAsTranslationDetails.otherDetails) &&
      Objects.equals(otherContributors, otherAsTranslationDetails.otherContributors);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), language, author, accreditation, versionLastTranslated, otherDetails);
    result = otherContributors == null ? 0 : 31 * result + otherContributors.hashCode();
    return result;
  }

  public TerminologyCode getLanguage() {
    return language;
  }

  public void setLanguage(TerminologyCode language) {
    this.language = language;
  }

  public Map<String, String> getAuthor() {
    return author;
  }

  public void setAuthor(Map<String, String> author) {
    this.author = author;
  }

  public @Nullable String getAccreditation() {
    return accreditation;
  }

  public void setAccreditation(@Nullable String accreditation) {
    this.accreditation = accreditation;
  }

  public @Nullable String getVersionLastTranslated() {
    return versionLastTranslated;
  }

  public void setVersionLastTranslated(@Nullable String versionLastTranslated) {
    this.versionLastTranslated = versionLastTranslated;
  }

  public @Nullable Map<String, String> getOtherDetails() {
    return otherDetails;
  }

  public void setOtherDetails(@Nullable Map<String, String> otherDetails) {
    this.otherDetails = otherDetails;
  }

  public @Nullable List<String> getOtherContributors() {
    return otherContributors;
  }

  public void setOtherContributors(@Nullable List<String> otherContributors) {
    this.otherContributors = otherContributors;
  }

  public String bmmClassName() {
    return "Translation_details";
  }

  @Override
  public String toString() {
    return "Translation_details";
  }
}
