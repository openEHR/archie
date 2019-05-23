package com.nedap.archie.rm.ehr;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RM Object for an Ehr. Not entirely sure if it is useful in a library. Generally, this could be something you map yourself
 * in some kind of persistence framework. But it can be useful for transferring information
 * Created by pieter.bos on 08/07/16.
 */
@XmlRootElement(name="EHR")
@XmlType(name = "EHR", propOrder = {
        "systemId",
        "ehrId",
        "timeCreated",
        "contributions",
        "ehrAccess",
        "ehrStatus",
        "directory",
        "compositions"
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Ehr extends RMObject {

    @XmlElement(name="system_id")
    private HierObjectId systemId;
    @XmlElement(name="ehr_id")
    private HierObjectId ehrId;

    private List<ObjectRef> contributions = new ArrayList<>();
    @XmlElement(name="ehr_status")
    private ObjectRef ehrStatus;
    @XmlElement(name="ehr_access")
    private ObjectRef ehrAccess;
    @Nullable
    private List<ObjectRef> compositions = new ArrayList<>();

    @Nullable
    private ObjectRef directory;

    @XmlElement(name="time_created")
    private DvDateTime timeCreated;

    public Ehr() {
    }

    public Ehr(HierObjectId systemId, HierObjectId ehrId, DvDateTime timeCreated, List<ObjectRef> contributions, ObjectRef ehrStatus, ObjectRef ehrAccess, @Nullable ObjectRef directory, @Nullable List<ObjectRef> compositions) {
        this.systemId = systemId;
        this.ehrId = ehrId;
        this.contributions = contributions;
        this.ehrStatus = ehrStatus;
        this.ehrAccess = ehrAccess;
        this.compositions = compositions;
        this.directory = directory;
        this.timeCreated = timeCreated;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ehr ehr = (Ehr) o;

        if (systemId != null ? !systemId.equals(ehr.systemId) : ehr.systemId != null) return false;
        if (ehrId != null ? !ehrId.equals(ehr.ehrId) : ehr.ehrId != null) return false;
        if (contributions != null ? !contributions.equals(ehr.contributions) : ehr.contributions != null) return false;
        if (ehrStatus != null ? !ehrStatus.equals(ehr.ehrStatus) : ehr.ehrStatus != null) return false;
        if (ehrAccess != null ? !ehrAccess.equals(ehr.ehrAccess) : ehr.ehrAccess != null) return false;
        if (compositions != null ? !compositions.equals(ehr.compositions) : ehr.compositions != null) return false;
        if (directory != null ? !directory.equals(ehr.directory) : ehr.directory != null) return false;
        return timeCreated != null ? timeCreated.equals(ehr.timeCreated) : ehr.timeCreated == null;

    }

    @Override
    public int hashCode() {
        int result = systemId != null ? systemId.hashCode() : 0;
        result = 31 * result + (ehrId != null ? ehrId.hashCode() : 0);
        result = 31 * result + (contributions != null ? contributions.hashCode() : 0);
        result = 31 * result + (ehrStatus != null ? ehrStatus.hashCode() : 0);
        result = 31 * result + (ehrAccess != null ? ehrAccess.hashCode() : 0);
        result = 31 * result + (compositions != null ? compositions.hashCode() : 0);
        result = 31 * result + (directory != null ? directory.hashCode() : 0);
        result = 31 * result + (timeCreated != null ? timeCreated.hashCode() : 0);
        return result;
    }
}
