package com.nedap.archie.rm.integration;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.composition.ContentItem;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 21/06/16.
 */
@XmlType(name = "GENERIC_ENTRY", propOrder = {
        "data"
})

public class GenericEntry extends ContentItem {
    private ItemTree data;

    public GenericEntry() {
    }

    public GenericEntry(String archetypeNodeId, DvText name, ItemTree data) {
        super(archetypeNodeId, name);
        this.data = data;
    }

    public GenericEntry(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, ItemTree data) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.data = data;
    }

    public ItemTree getData() {
        return data;
    }

    public void setData(ItemTree data) {
        this.data = data;
        setThisAsParent(data, "data");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GenericEntry that = (GenericEntry) o;

        return data != null ? data.equals(that.data) : that.data == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
