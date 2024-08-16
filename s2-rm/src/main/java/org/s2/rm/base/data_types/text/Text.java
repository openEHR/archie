package org.s2.rm.base.data_types.text;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Text
* BMM ancestors: Data_value
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Text", propOrder = {
  "text",
  "language",
  "encoding",
  "formatting"
})
public abstract class Text extends DataValue {
  /**
  * BMM name: text | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "text")
  private String text;

  /**
  * BMM name: language | BMM type: Terminology_code
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "language")
  private @Nullable TerminologyCode language;

  /**
  * BMM name: encoding | BMM type: Terminology_code
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "encoding")
  private @Nullable TerminologyCode encoding;

  /**
  * BMM name: formatting | BMM type: Text_format_types
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "formatting")
  private @Nullable TextFormatTypes formatting;

  public Text() {}

  public Text(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public @Nullable TerminologyCode getLanguage() {
    return language;
  }

  public void setLanguage(@Nullable TerminologyCode language) {
    this.language = language;
  }

  public @Nullable TerminologyCode getEncoding() {
    return encoding;
  }

  public void setEncoding(@Nullable TerminologyCode encoding) {
    this.encoding = encoding;
  }

  public @Nullable TextFormatTypes getFormatting() {
    return formatting;
  }

  public void setFormatting(@Nullable TextFormatTypes formatting) {
    this.formatting = formatting;
  }

  @Override
  public String bmmClassName() {
    return "Text";
  }

  @Override
  public String toString() {
    return "Text";
  }
}
