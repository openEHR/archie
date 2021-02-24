package com.nedap.archie.xml.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by pieter.bos on 22/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StringDictionaryItem", propOrder = {
        "value"
})
public class StringDictionaryItem {

    @XmlValue
    private String value;
    @XmlAttribute(name = "id", required = true)
    private String id;

    public StringDictionaryItem() {

    }

    public StringDictionaryItem(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}