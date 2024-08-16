package org.s2.rm.entity.physical_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Fiat_surface
* BMM ancestors: Material_location
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Fiat_surface", propOrder = {
  "uid",
  "isClosed"
})
public class FiatSurface extends MaterialLocation {
  /**
  * BMM name: is_closed | BMM type: Boolean
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "is_closed")
  private boolean isClosed;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public FiatSurface() {}

  public FiatSurface(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    FiatSurface otherAsFiatSurface = (FiatSurface) other;
    return Objects.equals(getDescription(), otherAsFiatSurface.getDescription()) &&
      Objects.equals(uid, otherAsFiatSurface.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsFiatSurface.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsFiatSurface.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsFiatSurface.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsFiatSurface.getFeederAudit()) &&
      Objects.equals(isClosed, otherAsFiatSurface.isClosed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid, isClosed);
  }

  public boolean getIsClosed() {
    return isClosed;
  }

  public void setIsClosed(boolean isClosed) {
    this.isClosed = isClosed;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Fiat_surface";
  }

  @Override
  public String toString() {
    return "Fiat_surface";
  }
}
