package com.nedap.archie.rm.datastructures;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import org.apache.commons.collections.CollectionUtils;

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
@XmlType(name = "ITEM_TREE", propOrder = {
        "items"
})
@XmlRootElement(name = "item_tree")
public class ItemTree extends ItemStructure<Item> {
    @Nullable
    private List<Item> items = new ArrayList<>();

    public ItemTree() {
    }

    public ItemTree(String archetypeNodeId, DvText name, @Nullable List<Item> items) {
        super(archetypeNodeId, name);
        setItems(items);
    }

    public ItemTree(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, @Nullable List<Item> items) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        setItems(items);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        setThisAsParent(items, "items");
    }

    public void addItem(Item item) {
        this.items.add(item);
        setThisAsParent(item, "items");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ItemTree itemTree = (ItemTree) o;

        if (items != null && itemTree.items == null) return false;
        if (items == null && itemTree.items != null) return false;

        // Compering elements ignoring order
        return CollectionUtils.isEqualCollection(items, itemTree.items);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
