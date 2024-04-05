package main.java.org.s2.rm.base.foundation_types.terminology;

import jakarta.annotation.Nonnull;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Terminology_term
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Terminology_term", propOrder = {
  "description",
  "concept"
})
public class TerminologyTerm {
  /**
  * BMM name: description | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "description")
  private @Nonnull String description;

  /**
  * BMM name: concept | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "concept")
  private @Nonnull TerminologyCode concept;

  public TerminologyTerm() {}

  public TerminologyTerm(@Nonnull String description, @Nonnull TerminologyCode concept) {
    this.description = description;
    this.concept = concept;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    TerminologyTerm otherAsTerminologyTerm = (TerminologyTerm) other;
    return Objects.equals(description, otherAsTerminologyTerm.description) &&
      Objects.equals(concept, otherAsTerminologyTerm.concept);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), description, concept);
  }

  public @Nonnull String getDescription() {
    return description;
  }

  public void setDescription(@Nonnull String description) {
    this.description = description;
  }

  public @Nonnull TerminologyCode getConcept() {
    return concept;
  }

  public void setConcept(@Nonnull TerminologyCode concept) {
    this.concept = concept;
  }

  public String bmmClassName() {
    return "Terminology_term";
  }

  @Override
  public String toString() {
    return "Terminology_term";
  }
}
