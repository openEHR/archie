package org.openehr.rm.support.identification;

import com.nedap.archie.base.RMObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 01/03/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OBJECT_ID", propOrder = {
        "value"
})
public abstract class ObjectId extends RMObject {
    @XmlElement
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
        return Objects.equals(value, objectId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
