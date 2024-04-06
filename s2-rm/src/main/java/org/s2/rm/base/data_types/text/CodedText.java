package org.s2.rm.base.data_types.text;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

/**
* BMM name: Coded_text
* BMM ancestors: Text
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Coded_text", propOrder = {
  "term"
})
public class CodedText extends Text {
  /**
  * BMM name: term | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "term")
  private TerminologyTerm term;

  public CodedText() {}

  public CodedText(TerminologyTerm term, String text) {
    super(text);
    this.term = term;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    CodedText otherAsCodedText = (CodedText) other;
    return Objects.equals(getText(), otherAsCodedText.getText()) &&
      Objects.equals(getLanguage(), otherAsCodedText.getLanguage()) &&
      Objects.equals(getEncoding(), otherAsCodedText.getEncoding()) &&
      Objects.equals(getFormatting(), otherAsCodedText.getFormatting()) &&
      Objects.equals(term, otherAsCodedText.term);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), term);
  }

  public TerminologyTerm getTerm() {
    return term;
  }

  public void setTerm(TerminologyTerm term) {
    this.term = term;
  }

  @Override
  public String bmmClassName() {
    return "Coded_text";
  }

  @Override
  public String toString() {
    return "Coded_text";
  }
}
