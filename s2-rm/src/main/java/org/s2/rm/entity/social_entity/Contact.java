package org.s2.rm.entity.social_entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.interval.Interval;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDate;
import org.s2.rm.base.model_support.archetyped.Archetyped;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Contact
* BMM ancestors: Locatable
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Contact", propOrder = {
  "uid",
  "timeValidity",
  "addresses"
})
public class Contact extends Locatable {
  /**
  * BMM name: time_validity | BMM type: Interval<Date>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "time_validity")
  private @Nullable Interval<RmDate> timeValidity;

  /**
  * BMM name: addresses | BMM type: List<Address>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "addresses")
  private @Nullable List<Address> addresses;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Contact() {}

  public Contact(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Contact otherAsContact = (Contact) other;
    return Objects.equals(uid, otherAsContact.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsContact.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsContact.getName()) &&
      Objects.equals(getCode(), otherAsContact.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsContact.getOriginalCode()) &&
      Objects.equals(getArchetypeDetails(), otherAsContact.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsContact.getFeederAudit()) &&
      Objects.equals(getLinks(), otherAsContact.getLinks()) &&
      Objects.equals(timeValidity, otherAsContact.timeValidity) &&
      Objects.equals(addresses, otherAsContact.addresses);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, timeValidity);
    result = addresses == null ? 0 : 31 * result + addresses.hashCode();
    return result;
  }

  public @Nullable Interval<RmDate> getTimeValidity() {
    return timeValidity;
  }

  public void setTimeValidity(@Nullable Interval<RmDate> timeValidity) {
    this.timeValidity = timeValidity;
  }

  public @Nullable List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(@Nullable List<Address> addresses) {
    this.addresses = addresses;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Contact";
  }

  @Override
  public String toString() {
    return "Contact";
  }
}
