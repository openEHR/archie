package com.nedap.archie.rm.ehr;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * RM Object for an Ehr. Not entirely sure if it is useful in a library. Generally, this could be something you map yourself
 * in some kind of persistence framework. But it can be useful for transferring information
 * Created by pieter.bos on 08/07/16.
 */
@XmlRootElement(name="EHR")
@XmlType(name="EHR")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ehr extends RMObject {

    private HierObjectId systemId;
    private HierObjectId ehrId;

    private List<ObjectRef> contributions = new ArrayList<>();
    private ObjectRef ehrStatus;
    private ObjectRef ehrAccess;
    @Nullable
    private List<ObjectRef> compositions = new ArrayList<>();

    @Nullable
    private ObjectRef directory;

    @XmlElement(name="time_created")
    private DvDateTime timeCreated;


    public HierObjectId getSystemId() {
        return systemId;
    }

    public void setSystemId(HierObjectId systemId) {
        this.systemId = systemId;
    }

    public HierObjectId getEhrId() {
        return ehrId;
    }

    public void setEhrId(HierObjectId ehrId) {
        this.ehrId = ehrId;
    }

    @Nullable
    public List<ObjectRef> getContributions() {
        return contributions;
    }

    public void setContributions(@Nullable List<ObjectRef> contributions) {
        this.contributions = contributions;
    }

    public void addContribution(ObjectRef contribution) {
        this.contributions.add(contribution);
    }

    public ObjectRef getEhrStatus() {
        return ehrStatus;
    }

    public void setEhrStatus(ObjectRef ehrStatus) {
        this.ehrStatus = ehrStatus;
    }

    public ObjectRef getEhrAccess() {
        return ehrAccess;
    }

    public void setEhrAccess(ObjectRef ehrAccess) {
        this.ehrAccess = ehrAccess;
    }

    @Nullable
    public List<ObjectRef> getCompositions() {
        return compositions;
    }

    public void setCompositions(@Nullable List<ObjectRef> compositions) {
        this.compositions = compositions;
    }

    public void addComposition(ObjectRef composition) {
        this.compositions.add(composition);
    }

    @Nullable
    public ObjectRef getDirectory() {
        return directory;
    }

    public void setDirectory(@Nullable ObjectRef directory) {
        this.directory = directory;
    }

    public DvDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(DvDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }
}
