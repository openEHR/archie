package com.nedap.archie.xml.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
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
