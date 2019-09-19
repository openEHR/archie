package com.nedap.archie.rm.changecontrol;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Version control object. To use this other than in data exchange purposed, you will need to create a subclass
 * and implement the methods
 * <p>
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VERSIONED_OBJECT")
public class VersionedObject<Type> extends RMObject {
    private HierObjectId uid;
    @XmlElement(name = "owner_id")
    private ObjectRef ownerId;

    @XmlElement(name = "time_created")
    private DvDateTime timeCreated;

    public VersionedObject() {
    }

    public VersionedObject(HierObjectId uid, ObjectRef ownerId, DvDateTime timeCreated) {
        this.uid = uid;
        this.ownerId = ownerId;
        this.timeCreated = timeCreated;
    }

    public HierObjectId getUid() {
        return uid;
    }

    public void setUid(HierObjectId uid) {
        this.uid = uid;
    }

    public ObjectRef getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ObjectRef ownerId) {
        this.ownerId = ownerId;
    }

    public DvDateTime getTimeCreated() {
        return timeCreated;
    }

    @JsonAlias({"time_creations"})
    public void setTimeCreated(DvDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionedObject<?> that = (VersionedObject<?>) o;
        return Objects.equals(uid, that.uid) &&
                Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(timeCreated, that.timeCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, ownerId, timeCreated);
    }
}
