package com.nedap.archie.rm.datastructures;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ITEM_LIST", propOrder = {
        "items"
})
@XmlRootElement(name = "item_list")
public class ItemList extends ItemStructure<Element> {

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

    @Nullable
    private List<Element> items = new ArrayList<>();

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

        if (items != null && itemList.items == null) return false;
        if (items == null && itemList.items != null) return false;

        // Compering elements ignoring order
        return items.equals(itemList.items);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
