package org.s2.rm.base.patterns.participation;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Party_self
* BMM ancestors: Party_proxy
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party_self")
public class PartySelf extends PartyProxy {
  public PartySelf() {}


  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PartySelf otherAsPartySelf = (PartySelf) other;
    return Objects.equals(getExternalRef(), otherAsPartySelf.getExternalRef());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Party_self";
  }

  @Override
  public String toString() {
    return "Party_self";
  }
}
