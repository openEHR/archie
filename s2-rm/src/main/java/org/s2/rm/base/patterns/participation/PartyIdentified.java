package org.s2.rm.base.patterns.participation;

import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.RweIdRef;
import org.s2.rm.base.model_support.identification.ObjectRef;

/**
* BMM name: Party_identified
* BMM ancestors: Party_proxy
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party_identified", propOrder = {
  "name",
  "identifiers"
})
public class PartyIdentified extends PartyProxy {
  /**
  * BMM name: name | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "name")
  private @Nullable String name;

  /**
  * BMM name: identifiers | BMM type: List<Rwe_id_ref>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "identifiers")
  private @Nullable List<RweIdRef> identifiers;


  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PartyIdentified otherAsPartyIdentified = (PartyIdentified) other;
    return Objects.equals(getExternalRef(), otherAsPartyIdentified.getExternalRef()) &&
      Objects.equals(name, otherAsPartyIdentified.name) &&
      Objects.equals(identifiers, otherAsPartyIdentified.identifiers);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), name);
    result = identifiers == null ? 0 : 31 * result + identifiers.hashCode();
    return result;
  }

  public @Nullable String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public @Nullable List<RweIdRef> getIdentifiers() {
    return identifiers;
  }

  public void setIdentifiers(@Nullable List<RweIdRef> identifiers) {
    this.identifiers = identifiers;
  }

  @Override
  public String bmmClassName() {
    return "Party_identified";
  }

  @Override
  public String toString() {
    return "Party_identified";
  }
}
