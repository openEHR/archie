package org.s2.rm.base.data_types.text;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Plain_text
* BMM ancestors: Text
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Plain_text")
public class PlainText extends Text {
  public PlainText() {}

  public PlainText(String text) {
    super(text);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PlainText otherAsPlainText = (PlainText) other;
    return Objects.equals(getText(), otherAsPlainText.getText()) &&
      Objects.equals(getLanguage(), otherAsPlainText.getLanguage()) &&
      Objects.equals(getEncoding(), otherAsPlainText.getEncoding()) &&
      Objects.equals(getFormatting(), otherAsPlainText.getFormatting());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Plain_text";
  }

  @Override
  public String toString() {
    return "Plain_text";
  }
}
