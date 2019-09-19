package com.nedap.archie.rm.support.identification;

import com.nedap.archie.rm.RMObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

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
        return Objects.equals(namespace, objectRef.namespace) &&
                Objects.equals(type, objectRef.type) &&
                Objects.equals(id, objectRef.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, type, id);
    }
}
