package com.nedap.archie.rm.support.identification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * TODO: id should be redefined as UID_BASED_ID. Not sure how
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LOCATABLE_REF", propOrder = {
        "path"
})
public class LocatableRef extends ObjectRef<UIDBasedId> {
    private String path;


    public LocatableRef() {
    }

    public LocatableRef(UIDBasedId id, String namespace, String type, String path) {
        super(id, namespace, type);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        LocatableRef that = (LocatableRef) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
