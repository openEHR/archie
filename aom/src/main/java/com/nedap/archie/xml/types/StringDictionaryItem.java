package com.nedap.archie.xml.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

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