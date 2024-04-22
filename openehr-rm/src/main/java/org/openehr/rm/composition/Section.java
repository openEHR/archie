package org.openehr.rm.composition;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

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
@XmlRootElement
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
        return Objects.equals(items, section.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), items);
    }

    @Invariant(value="Items_valid", ignored=true)
    public boolean itemsValid() {
        return InvariantUtil.nullOrNotEmpty(items);
    }
}
