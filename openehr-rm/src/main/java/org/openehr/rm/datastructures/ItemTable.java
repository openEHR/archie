package org.openehr.rm.datastructures;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rminfo.RMPropertyIgnore;

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
@XmlType(name = "ITEM_TABLE", propOrder = {
        "rows"
})
@XmlRootElement(name = "item_table")
public class ItemTable extends ItemStructure {

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
        for (Cluster row : rows) {
            for (Item element : row.getItems()) {
                result.add((Element) element);
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
        return Objects.equals(rows, itemTable.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rows);
    }

    @Invariant("Valid_structure")
    public boolean validStructure() {
        if(rows != null) {
            for(Cluster row:rows) {
                if(row.getItems() != null) {
                    for(Item item:row.getItems()) {
                        if(!(item instanceof Element)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Invariant("Valid_number_of_rows")//indicated in text only, but hey, it's a rule that must be validated
    public boolean validNumberOfRows() {
        if(rows != null) {
            Integer size = null;
            for(Cluster row:rows) {
                if(row.getItems() != null) {
                    if(size == null) {
                        size = row.getItems().size();
                    } else {
                        if(size != row.getItems().size()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
