package org.s2.rm.base.data_types.uri;


import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Ehr_uri_ref
* BMM ancestors: Uri_ref
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ehr_uri_ref")
public class EhrUriRef extends UriRef {
  public EhrUriRef() {}

  public EhrUriRef(String value) {
    super(value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    EhrUriRef otherAsEhrUriRef = (EhrUriRef) other;
    return Objects.equals(getValue(), otherAsEhrUriRef.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String bmmClassName() {
    return "Ehr_uri_ref";
  }

  @Override
  public String toString() {
    return "Ehr_uri_ref";
  }
}
