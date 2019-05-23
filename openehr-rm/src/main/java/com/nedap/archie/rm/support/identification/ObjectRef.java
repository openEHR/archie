package com.nedap.archie.rm.support.identification;

import com.nedap.archie.rm.RMObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OBJECT_REF", propOrder = {
        "id",
        "namespace",
        "type"
})
public class ObjectRef<Idtype extends ObjectId> extends RMObject {
    private String namespace;
    private String type;
    private Idtype id;

    public ObjectRef() {
    }

    public ObjectRef(Idtype id, String namespace, String type) {
        this.namespace = namespace;
        this.type = type;
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Idtype getId() {
        return id;
    }

    public void setId(Idtype id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectRef<?> objectRef = (ObjectRef<?>) o;

        if (namespace != null ? !namespace.equals(objectRef.namespace) : objectRef.namespace != null) return false;
        if (type != null ? !type.equals(objectRef.type) : objectRef.type != null) return false;
        return id != null ? id.equals(objectRef.id) : objectRef.id == null;

    }

    @Override
    public int hashCode() {
        int result = namespace != null ? namespace.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
