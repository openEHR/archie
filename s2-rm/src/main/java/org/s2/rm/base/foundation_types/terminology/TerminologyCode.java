package org.s2.rm.base.foundation_types.terminology;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.primitive_types.Uri;

/**
* BMM name: Terminology_code
* isAbstract: false | isPrimitiveType: true | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Terminology_code", propOrder = {
  "terminologyId",
  "terminologyVersion",
  "codeString",
  "uri"
})
public class TerminologyCode {
  /**
  * BMM name: terminology_id | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "terminology_id")
  private String terminologyId;

  /**
  * BMM name: terminology_version | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "terminology_version")
  private @Nullable String terminologyVersion;

  /**
  * BMM name: code_string | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "code_string")
  private String codeString;

  /**
  * BMM name: uri | BMM type: Uri
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "uri")
  private @Nullable Uri uri;

  public TerminologyCode() {}

  public TerminologyCode(String terminologyId, String codeString) {
    this.terminologyId = terminologyId;
    this.codeString = codeString;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    TerminologyCode otherAsTerminologyCode = (TerminologyCode) other;
    return Objects.equals(terminologyId, otherAsTerminologyCode.terminologyId) &&
      Objects.equals(terminologyVersion, otherAsTerminologyCode.terminologyVersion) &&
      Objects.equals(codeString, otherAsTerminologyCode.codeString) &&
      Objects.equals(uri, otherAsTerminologyCode.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), terminologyId, terminologyVersion, codeString, uri);
  }

  public String getTerminologyId() {
    return terminologyId;
  }

  public void setTerminologyId(String terminologyId) {
    this.terminologyId = terminologyId;
  }

  public @Nullable String getTerminologyVersion() {
    return terminologyVersion;
  }

  public void setTerminologyVersion(@Nullable String terminologyVersion) {
    this.terminologyVersion = terminologyVersion;
  }

  public String getCodeString() {
    return codeString;
  }

  public void setCodeString(String codeString) {
    this.codeString = codeString;
  }

  public @Nullable Uri getUri() {
    return uri;
  }

  public void setUri(@Nullable Uri uri) {
    this.uri = uri;
  }

  public String bmmClassName() {
    return "Terminology_code";
  }

  @Override
  public String toString() {
    return "Terminology_code";
  }
}
