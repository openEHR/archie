package com.nedap.archie.rm.datastructures;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.RMPropertyIgnore;
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
@XmlType(name = "ITEM_TABLE", propOrder = {
        "rows"
})
@XmlRootElement(name = "item_table")
public class ItemTable extends ItemStructure<Element> {

    @Nullable
    private List<Cluster> rows = new ArrayList<>();


    public ItemTable() {
    }

    public ItemTable(String archetypeNodeId, DvText name, @Nullable List<Cluster> rows) {
        super(archetypeNodeId, name);
        setRows(rows);
    }

    public ItemTable(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, @Nullable List<Cluster> rows) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        setRows(rows);
    }

    public List<Cluster> getRows() {
        return rows;
    }

    public void setRows(List<Cluster> rows) {
        this.rows = rows;
        setThisAsParent(rows, "rows");
    }

    public void addItem(Cluster row) {
        this.rows.add(row);
        setThisAsParent(row, "rows");
    }

    /**
     * This is a bit of a strange one - returns all elements present in the table. Use getRows instead
     */
    @Override
    @RMPropertyIgnore
    public List<Element> getItems() {
        if (rows == null) {
            return null;
        }
        List<Element> result = new ArrayList<>();
        for (Cluster<Element> row : rows) {
            for (Element element : row.getItems()) {
                result.add(element);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ItemTable itemTable = (ItemTable) o;

        if (rows != null && itemTable.rows == null) return false;
        if (rows == null && itemTable.rows != null) return false;

        // Compering elements ignoring order
        return CollectionUtils.isEqualCollection(rows, itemTable.rows);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        return result;
    }
}
