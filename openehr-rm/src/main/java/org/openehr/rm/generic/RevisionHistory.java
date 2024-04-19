package org.openehr.rm.generic;

import com.nedap.archie.base.RMObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "REVISION_HISTORY")
public class RevisionHistory extends RMObject {


    public RevisionHistory() {
    }

    public RevisionHistory(List<RevisionHistoryItem> items) {
        this.items = items;
    }

    private List<RevisionHistoryItem> items = new ArrayList<>();

    public List<RevisionHistoryItem> getItems() {
        return items;
    }

    public void setItems(List<RevisionHistoryItem> items) {
        this.items = items;
    }

    public void addItem(RevisionHistoryItem item) {
        this.items.add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevisionHistory that = (RevisionHistory) o;
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }
}
