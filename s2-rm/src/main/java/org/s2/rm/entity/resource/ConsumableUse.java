package org.s2.rm.entity.resource;

import java.math.BigDecimal;
import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.time.RmDateTime;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.identification.Uuid;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Consumable_use
* BMM ancestors: Resource_use
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Consumable_use", propOrder = {
  "uid",
  "amount"
})
public class ConsumableUse extends ResourceUse {
  /**
  * BMM name: amount | BMM type: Decimal
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "amount")
  private BigDecimal amount;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public ConsumableUse() {}

  public ConsumableUse(RmDateTime startTime, String archetypeNodeId, String name) {
    super(startTime, archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ConsumableUse otherAsConsumableUse = (ConsumableUse) other;
    return Objects.equals(getStartTime(), otherAsConsumableUse.getStartTime()) &&
      Objects.equals(getDuration(), otherAsConsumableUse.getDuration()) &&
      Objects.equals(getCostData(), otherAsConsumableUse.getCostData()) &&
      Objects.equals(getDescription(), otherAsConsumableUse.getDescription()) &&
      Objects.equals(uid, otherAsConsumableUse.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsConsumableUse.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsConsumableUse.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsConsumableUse.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsConsumableUse.getFeederAudit()) &&
      Objects.equals(amount, otherAsConsumableUse.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), uid, amount);
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Consumable_use";
  }

  @Override
  public String toString() {
    return "Consumable_use";
  }
}
