package org.openehr.rm.integration;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.composition.ContentItem;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

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
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
