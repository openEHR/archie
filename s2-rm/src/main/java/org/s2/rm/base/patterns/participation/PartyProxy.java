package org.s2.rm.base.patterns.participation;

import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

import com.nedap.archie.base.RMObject;
import org.s2.rm.base.model_support.identification.ObjectRef;

/**
* BMM name: Party_proxy
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party_proxy", propOrder = {
  "externalRef"
})
public abstract class PartyProxy extends RMObject {
  /**
  * BMM name: external_ref | BMM type: Object_ref
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "external_ref")
  private @Nullable ObjectRef externalRef;


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
