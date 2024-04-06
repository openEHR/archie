package org.s2.rm.base.data_types.encapsulated;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.primitive_types.Uri;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Parsable
* BMM ancestors: Encapsulated
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Parsable", propOrder = {
  "text",
  "schema",
  "formalism",
  "encoding"
})
public class Parsable extends Encapsulated {
  /**
  * BMM name: text | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "text")
  private @Nullable String text;

  /**
  * BMM name: schema | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "schema")
  private @Nullable String schema;

  /**
  * BMM name: formalism | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "formalism")
  private String formalism;

  /**
  * BMM name: encoding | BMM type: Terminology_code
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "encoding")
  private @Nullable TerminologyCode encoding;

  public Parsable() {}

  public Parsable(String formalism) {
    this.formalism = formalism;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Parsable otherAsParsable = (Parsable) other;
    return Objects.equals(getLanguage(), otherAsParsable.getLanguage()) &&
      Objects.equals(getUri(), otherAsParsable.getUri()) &&
      Objects.equals(text, otherAsParsable.text) &&
      Objects.equals(schema, otherAsParsable.schema) &&
      Objects.equals(formalism, otherAsParsable.formalism) &&
      Objects.equals(encoding, otherAsParsable.encoding);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), text, schema, formalism, encoding);
  }

  public @Nullable String getText() {
    return text;
  }

  public void setText(@Nullable String text) {
    this.text = text;
  }

  public @Nullable String getSchema() {
    return schema;
  }

  public void setSchema(@Nullable String schema) {
    this.schema = schema;
  }

  public String getFormalism() {
    return formalism;
  }

  public void setFormalism(String formalism) {
    this.formalism = formalism;
  }

  public @Nullable TerminologyCode getEncoding() {
    return encoding;
  }

  public void setEncoding(@Nullable TerminologyCode encoding) {
    this.encoding = encoding;
  }

  @Override
  public String bmmClassName() {
    return "Parsable";
  }

  @Override
  public String toString() {
    return "Parsable";
  }
}
