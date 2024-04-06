package org.s2.rm.entity;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.interval.Interval;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.foundation_types.time.RmDate;
import org.s2.rm.base.model_support.archetyped.Locatable;
import org.s2.rm.base.model_support.identification.ObjectRef;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Entity_relationship
* BMM ancestors: Locatable
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Entity_relationship", propOrder = {
  "type",
  "source",
  "target",
  "description",
  "timeValidity"
})
public abstract class EntityRelationship extends Locatable {
  /**
  * BMM name: type | BMM type: Terminology_term
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "type")
  private TerminologyTerm type;

  /**
  * BMM name: source | BMM type: Object_ref
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "source")
  private ObjectRef source;

  /**
  * BMM name: target | BMM type: Object_ref
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "target")
  private ObjectRef target;

  /**
  * BMM name: description | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "description")
  private @Nullable List<Node> description;

  /**
  * BMM name: time_validity | BMM type: Interval<Date>
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "time_validity")
  private @Nullable Interval<RmDate> timeValidity;

  public EntityRelationship() {}

  public EntityRelationship(TerminologyTerm type, ObjectRef source, ObjectRef target, String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
    this.type = type;
    this.source = source;
    this.target = target;
  }

  public TerminologyTerm getType() {
    return type;
  }

  public void setType(TerminologyTerm type) {
    this.type = type;
  }

  public ObjectRef getSource() {
    return source;
  }

  public void setSource(ObjectRef source) {
    this.source = source;
  }

  public ObjectRef getTarget() {
    return target;
  }

  public void setTarget(ObjectRef target) {
    this.target = target;
  }

  public @Nullable List<Node> getDescription() {
    return description;
  }

  public void setDescription(@Nullable List<Node> description) {
    this.description = description;
  }

  public @Nullable Interval<RmDate> getTimeValidity() {
    return timeValidity;
  }

  public void setTimeValidity(@Nullable Interval<RmDate> timeValidity) {
    this.timeValidity = timeValidity;
  }

  @Override
  public String bmmClassName() {
    return "Entity_relationship";
  }

  @Override
  public String toString() {
    return "Entity_relationship";
  }
}
