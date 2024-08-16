package org.s2.rm.base.patterns.data_structures;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.data_types.DataValue;
import org.s2.rm.base.data_types.text.Text;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.model_support.archetyped.FeederAudit;
import org.s2.rm.base.model_support.archetyped.InfoItem;
import org.s2.rm.base.model_support.archetyped.Link;
import org.s2.rm.base.model_support.identification.Uuid;

/**
* BMM name: Node
* BMM ancestors: Info_item
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Node", propOrder = {
  "uid",
  "value",
  "originalValue",
  "nullFlavor",
  "nullReason",
  "items"
})
public class Node extends InfoItem {
  /**
  * BMM name: value | BMM type: Data_value
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "value")
  private @Nullable DataValue value;

  /**
  * BMM name: original_value | BMM type: Data_value
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "original_value")
  private @Nullable DataValue originalValue;

  /**
  * BMM name: null_flavor | BMM type: Terminology_term
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  * valueConstraint: s2.NullFlavor
  */
  @XmlElement(name = "null_flavor")
  private @Nullable TerminologyTerm nullFlavor;

  /**
  * BMM name: null_reason | BMM type: Text
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "null_reason")
  private @Nullable Text nullReason;

  /**
  * BMM name: items | BMM type: List<{@literal Node}>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "items")
  private @Nullable List<Node> items;


  // Properties added from the extended class: Locatable

  /**
  * BMM name: uid | BMM type: Uuid
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: true | existence: 0..1
  */
  @XmlElement(name = "uid")
  private @Nullable Uuid uid;

  public Node() {}

  public Node(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Node otherAsNode = (Node) other;
    return Objects.equals(getCode(), otherAsNode.getCode()) &&
      Objects.equals(getOriginalCode(), otherAsNode.getOriginalCode()) &&
      Objects.equals(getLinks(), otherAsNode.getLinks()) &&
      Objects.equals(uid, otherAsNode.uid) &&
      Objects.equals(getArchetypeNodeId(), otherAsNode.getArchetypeNodeId()) &&
      Objects.equals(getName(), otherAsNode.getName()) &&
      Objects.equals(getArchetypeDetails(), otherAsNode.getArchetypeDetails()) &&
      Objects.equals(getFeederAudit(), otherAsNode.getFeederAudit()) &&
      Objects.equals(value, otherAsNode.value) &&
      Objects.equals(originalValue, otherAsNode.originalValue) &&
      Objects.equals(nullFlavor, otherAsNode.nullFlavor) &&
      Objects.equals(nullReason, otherAsNode.nullReason) &&
      Objects.equals(items, otherAsNode.items);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), uid, value, originalValue, nullFlavor, nullReason);
    result = items == null ? 0 : 31 * result + items.hashCode();
    return result;
  }

  public @Nullable DataValue getValue() {
    return value;
  }

  public void setValue(@Nullable DataValue value) {
    this.value = value;
  }

  public @Nullable DataValue getOriginalValue() {
    return originalValue;
  }

  public void setOriginalValue(@Nullable DataValue originalValue) {
    this.originalValue = originalValue;
  }

  public @Nullable TerminologyTerm getNullFlavor() {
    return nullFlavor;
  }

  public void setNullFlavor(@Nullable TerminologyTerm nullFlavor) {
    this.nullFlavor = nullFlavor;
  }

  public @Nullable Text getNullReason() {
    return nullReason;
  }

  public void setNullReason(@Nullable Text nullReason) {
    this.nullReason = nullReason;
  }

  public @Nullable List<Node> getItems() {
    return items;
  }

  public void setItems(@Nullable List<Node> items) {
    this.items = items;
  }

  public @Nullable Uuid getUid() {
    return uid;
  }

  public void setUid(@Nullable Uuid uid) {
    this.uid = uid;
  }

  @Override
  public String bmmClassName() {
    return "Node";
  }

  @Override
  public String toString() {
    return "Node";
  }
}
