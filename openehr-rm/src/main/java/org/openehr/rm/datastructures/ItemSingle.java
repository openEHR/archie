package org.openehr.rm.datastructures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.RMPropertyIgnore;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

/**
 * added constraint is that this contains only one item
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ITEM_SINGLE", propOrder = {
        "item"
})
public class ItemSingle extends ItemStructure {

    private Element item;


    public ItemSingle() {
    }

    public ItemSingle(String archetypeNodeId, DvText name, Element item) {
        super(archetypeNodeId, name);
        this.item = item;
    }

    public ItemSingle(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, Element item) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.item = item;
    }

    public Element getItem() {
        return item;
    }

    public void setItem(Element item) {
        this.item = item;
    }

    @Override
    @RMPropertyIgnore
    @JsonIgnore
    @XmlTransient
    public List<Element> getItems() {
        return Lists.newArrayList(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ItemSingle that = (ItemSingle) o;
        return Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), item);
    }
}
