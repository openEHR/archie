package org.s2.rm.care.composition;

import javax.xml.bind.annotation.*;
import org.s2.rm.base.model_support.archetyped.InfoItem;

/**
* BMM name: Content_item
* BMM ancestors: Info_item
* isAbstract: true | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Content_item")
public abstract class ContentItem extends InfoItem {
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
