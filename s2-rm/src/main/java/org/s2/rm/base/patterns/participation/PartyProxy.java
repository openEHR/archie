package org.s2.rm.base.patterns.participation;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.identification.ObjectRef;

/**
* BMM name: Party_proxy
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party_proxy", propOrder = {
  "externalRef"
})
public abstract class PartyProxy {
  /**
  * BMM name: external_ref | BMM type: Object_ref
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "external_ref")
  private @Nullable ObjectRef externalRef;

  public PartyProxy() {}


  public @Nullable ObjectRef getExternalRef() {
    return externalRef;
  }

  public void setExternalRef(@Nullable ObjectRef externalRef) {
    this.externalRef = externalRef;
  }

  public String bmmClassName() {
    return "Party_proxy";
  }

  @Override
  public String toString() {
    return "Party_proxy";
  }
}
