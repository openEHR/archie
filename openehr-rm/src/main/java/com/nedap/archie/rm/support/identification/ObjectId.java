package com.nedap.archie.rm.support.identification;

import com.nedap.archie.rm.RMObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 01/03/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OBJECT_ID", propOrder = {
        "value"
})
public abstract class ObjectId extends RMObject {

    private String value;

    public ObjectId() {
    }

    public ObjectId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectId objectId = (ObjectId) o;

        return value != null ? value.equals(objectId.value) : objectId.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
