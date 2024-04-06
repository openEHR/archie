package org.s2.rm.base.data_types.encapsulated;

import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;
import org.s2.rm.base.foundation_types.primitive_types.Uri;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Encapsulated
* BMM ancestors: Data_value
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Encapsulated", propOrder = {
  "language",
  "uri"
})
public abstract class Encapsulated extends DataValue {
  /**
  * BMM name: language | BMM type: Terminology_code
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "language")
  private @Nullable TerminologyCode language;

  /**
  * BMM name: uri | BMM type: Uri
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "uri")
  private @Nullable Uri uri;


  public @Nullable TerminologyCode getLanguage() {
    return language;
  }

  public void setLanguage(@Nullable TerminologyCode language) {
    this.language = language;
  }

  public @Nullable Uri getUri() {
    return uri;
  }

  public void setUri(@Nullable Uri uri) {
    this.uri = uri;
  }

  @Override
  public String bmmClassName() {
    return "Encapsulated";
  }

  @Override
  public String toString() {
    return "Encapsulated";
  }
}
