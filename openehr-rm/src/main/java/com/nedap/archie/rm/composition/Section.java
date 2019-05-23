package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SECTION", propOrder = {
        "items"
})
public class Section extends ContentItem {

    @Nullable
    private List<ContentItem> items = new ArrayList<>();

    public Section() {
    }

    public Section(String archetypeNodeId, DvText name, @Nullable List<ContentItem> items) {
        super(archetypeNodeId, name);
        this.items = items;
    }

    public Section(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, @Nullable List<ContentItem> items) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.items = items;
    }

    public List<ContentItem> getItems() {
        return items;
    }

    public void setItems(List<ContentItem> items) {
        this.items = items;
        setThisAsParent(items, "items");
    }

    public void addItem(ContentItem item) {
        this.items.add(item);
        setThisAsParent(item, "items");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Section section = (Section) o;

        return items != null ? items.equals(section.items) : section.items == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
