package org.openehr.rm.datavalues;

/**
 * Created by pieter.bos on 08/07/16.
 */

import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_PARAGRAPH")
@Deprecated
public class DvParagraph extends DataValue {

    private List<DvText> items = new ArrayList<>();

    public List<DvText> getItems() {
        return items;
    }

    public void setItems(List<DvText> items) {
        this.items = items;
    }

    public void addItem(DvText item) {
        this.items.add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DvParagraph that = (DvParagraph) o;
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Invariant(value = "Items_valid", ignored = true)
    public boolean itemsValid() {
        return InvariantUtil.nullOrNotEmpty(items);
    }
}
