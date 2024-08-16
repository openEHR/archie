package org.s2.rm.entity.physical_entity;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Geographical_site
* BMM ancestors: Fiat_line
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Geographical_site", propOrder = {
  "uid"
})
public class GeographicalSite extends FiatLine {

  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public GeographicalSite() {}

  public GeographicalSite(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    GeographicalSite otherAsGeographicalSite = (GeographicalSite) other;
    return Objects.equals(getIsClosed(), otherAsGeographicalSite.getIsClosed()) &&
      Objects.equals(getDescription(), otherAsGeographicalSite.getDescription()) &&
      Objects.equals(uid, otherAsGeographicalSite.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsGeographicalSite.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsGeographicalSite.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsGeographicalSite.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsGeographicalSite.getFeederAudit());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid);
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Geographical_site";
  }

  @Override
  public String toString() {
    return "Geographical_site";
  }
}
