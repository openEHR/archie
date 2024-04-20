package org.openehr.rm.datastructures;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ITEM_LIST", propOrder = {
        "items"
})
@XmlRootElement(name = "item_list")
public class ItemList extends ItemStructure {


    @Nullable
    private List<Element> items = new ArrayList<>();

    public ItemList() {
    }

    public ItemList(String archetypeNodeId, DvText name, @Nullable List<Element> items) {
        super(archetypeNodeId, name);
        setItems(items);
    }

    public ItemList(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, @Nullable List<Element> items) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        setItems(items);
    }

    @Override
    public List<Element> getItems() {
        return items;
    }

    public void setItems(List<Element> items) {
        this.items = items;
        setThisAsParent(items, "items");
    }

    public void addItem(Element item) {
        this.items.add(item);
        setThisAsParent(item, "items");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ItemList itemList = (ItemList) o;
        return Objects.equals(items, itemList.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), items);
    }
}
