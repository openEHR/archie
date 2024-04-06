package org.s2.rm.base.model_support.identification;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Iso_oid
* BMM ancestors: Primitive_id
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Iso_oid")
public class IsoOid extends PrimitiveId {
  public IsoOid() {}

  public IsoOid(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    IsoOid otherAsIsoOid = (IsoOid) other;
    return Objects.equals(getValue(), otherAsIsoOid.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Iso_oid";
  }

  @Override
  public String toString() {
    return "Iso_oid";
  }
}
