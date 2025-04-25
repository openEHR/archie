package com.nedap.archie.xml.types;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by pieter.bos on 26/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TermBindingSet", propOrder = {
        "items"
})
public class TermBindingSet {

    @XmlElement(type = StringDictionaryItem.class)
    protected List<StringDictionaryItem> items;
    @XmlAttribute(name = "id", required = true)
    protected String id;

    public List<StringDictionaryItem> getItems() {
        return items;
    }

    public void setItems(List<StringDictionaryItem> items) {
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
