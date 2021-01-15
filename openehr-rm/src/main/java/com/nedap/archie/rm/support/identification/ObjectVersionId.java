package com.nedap.archie.rm.support.identification;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OBJECT_VERSION_ID")
public class ObjectVersionId extends UIDBasedId {

    public ObjectVersionId() {
    }

    public ObjectVersionId(String value) {
        super(value);
    }

    @JsonIgnore
    @XmlTransient
    public UID getObjectId() {
        return getRoot();
    }

    @JsonIgnore
    @XmlTransient
    public UID getCreatingSystemId() {
        if (getExtension() == null)
            // TODO: what kind of exception should be thrown here?
            throw new IllegalArgumentException("Invalid OBJECT_VERSION_ID. Needs to have EXTENSION.");

        int index = getExtension().indexOf("::");
        String system;
        if (index != -1)
            system = getExtension().substring(0, index);
        else
            // TODO: what kind of exception should be thrown here?
            throw new IllegalArgumentException("Invalid OBJECT_VERSION_ID. Needs to have CREATING_SYSTEM_ID.");

        return new UUID(system);
    }

    @JsonIgnore
    @XmlTransient
    public VersionTreeId getVersionTreeId() {
        if (getExtension() == null)
            // TODO: what kind of exception should be thrown here?
            throw new IllegalArgumentException("Invalid OBJECT_VERSION_ID. Needs to have EXTENSION.");

        int index = getExtension().indexOf("::");
        String version;
        if (index != -1)
            version = getExtension().substring(index+2);
        else
            // TODO: what kind of exception should be thrown here?
            throw new IllegalArgumentException("Invalid OBJECT_VERSION_ID. Needs to have CREATING_SYSTEM_ID.");

        return  new VersionTreeId(version);
    }

    @JsonIgnore
    @XmlTransient
    public Boolean isBranch() {
        return getVersionTreeId().isBranch();
    }
}
