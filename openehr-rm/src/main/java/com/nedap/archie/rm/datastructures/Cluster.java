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
@XmlType(name = "CLUSTER", propOrder = {
        "items"
})
@XmlRootElement
public class Cluster<Type extends Item> extends Item {

    private List<Type> items = new ArrayList<>();

    public Cluster() {
    }

    public Cluster(String archetypeNodeId, DvText name, List<Type> items) {
        super(archetypeNodeId, name);
        setItems(items);
    }

    public Cluster(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, List<Type> items) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        setItems(items);
    }

    public List<Type> getItems() {
        return items;
    }

    public void setItems(List<Type> items) {
        this.items = items;

        setThisAsParent(items, "items");

    }

    public void addItem(Type item) {
        items.add(item);
        setThisAsParent(item, "items");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Cluster<?> cluster = (Cluster<?>) o;

        if (items != null && cluster.items == null) return false;
        if (items == null && cluster.items != null) return false;

        // Compering elements ignoring order
        return CollectionUtils.isEqualCollection(items, cluster.items);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
