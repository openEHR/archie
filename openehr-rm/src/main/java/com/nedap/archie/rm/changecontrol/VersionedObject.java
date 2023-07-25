package com.nedap.archie.rm.changecontrol;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
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
    private ObjectRef<? extends ObjectId> ownerId;

    @XmlElement(name = "time_created")
    private DvDateTime timeCreated;

    public VersionedObject() {
    }

    public VersionedObject(HierObjectId uid, ObjectRef<? extends ObjectId> ownerId, DvDateTime timeCreated) {
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

    public ObjectRef<? extends ObjectId> getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ObjectRef<? extends ObjectId> ownerId) {
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
