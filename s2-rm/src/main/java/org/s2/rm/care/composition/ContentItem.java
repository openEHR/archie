package org.s2.rm.care.composition;


import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.Locatable;

/**
* BMM name: Content_item
* BMM ancestors: Locatable
* isAbstract: true | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Content_item")
public abstract class ContentItem extends Locatable {
  public ContentItem() {}

  public ContentItem(String archetypeNodeId, String name) {
    super(archetypeNodeId, name);
  }

  @Override
  public String bmmClassName() {
    return "Content_item";
  }

  @Override
  public String toString() {
    return "Content_item";
  }
}
