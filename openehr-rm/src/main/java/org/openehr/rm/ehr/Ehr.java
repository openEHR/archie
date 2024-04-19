package org.openehr.rm.ehr;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        "compositions",
        "folders"
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Ehr extends RMObject {

    @XmlElement(name="system_id")
    private HierObjectId systemId;
    @XmlElement(name="ehr_id")
    private HierObjectId ehrId;

    private List<ObjectRef<? extends ObjectId>> contributions = new ArrayList<>();
    @XmlElement(name="ehr_status")
    private ObjectRef<? extends ObjectId> ehrStatus;
    @XmlElement(name="ehr_access")
    private ObjectRef<? extends ObjectId> ehrAccess;
    @Nullable
    private List<ObjectRef<? extends ObjectId>> compositions = new ArrayList<>();

    @Nullable
    private ObjectRef<? extends ObjectId> directory;

    @Nullable
    private List<ObjectRef<? extends ObjectId>> folders = new ArrayList<>();

    @XmlElement(name="time_created")
    private DvDateTime timeCreated;

    public Ehr() {
    }

    public Ehr(HierObjectId systemId, HierObjectId ehrId, DvDateTime timeCreated, List<ObjectRef<? extends ObjectId>> contributions, ObjectRef<? extends ObjectId> ehrStatus, ObjectRef<? extends ObjectId> ehrAccess, @Nullable ObjectRef<? extends ObjectId> directory, @Nullable List<ObjectRef<? extends ObjectId>> compositions) {
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
    public List<ObjectRef<? extends ObjectId>> getContributions() {
        return contributions;
    }

    public void setContributions(@Nullable List<ObjectRef<? extends ObjectId>> contributions) {
        this.contributions = contributions;
    }

    public void addContribution(ObjectRef<? extends ObjectId> contribution) {
        this.contributions.add(contribution);
    }

    public ObjectRef<? extends ObjectId> getEhrStatus() {
        return ehrStatus;
    }

    public void setEhrStatus(ObjectRef<? extends ObjectId> ehrStatus) {
        this.ehrStatus = ehrStatus;
    }

    public ObjectRef<? extends ObjectId> getEhrAccess() {
        return ehrAccess;
    }

    public void setEhrAccess(ObjectRef<? extends ObjectId> ehrAccess) {
        this.ehrAccess = ehrAccess;
    }

    @Nullable
    public List<ObjectRef<? extends ObjectId>> getCompositions() {
        return compositions;
    }

    public void setCompositions(@Nullable List<ObjectRef<? extends ObjectId>> compositions) {
        this.compositions = compositions;
    }

    public void addComposition(ObjectRef<? extends ObjectId> composition) {
        this.compositions.add(composition);
    }

    @Nullable
    public ObjectRef<? extends ObjectId> getDirectory() {
        return directory;
    }

    public void setDirectory(@Nullable ObjectRef<? extends ObjectId> directory) {
        this.directory = directory;
    }

    public DvDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(DvDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Nullable
    public List<ObjectRef<? extends ObjectId>> getFolders() {
        return folders;
    }

    public void setFolders(@Nullable List<ObjectRef<? extends ObjectId>> folders) {
        this.folders = folders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ehr ehr = (Ehr) o;
        return Objects.equals(systemId, ehr.systemId) &&
                Objects.equals(ehrId, ehr.ehrId) &&
                Objects.equals(contributions, ehr.contributions) &&
                Objects.equals(ehrStatus, ehr.ehrStatus) &&
                Objects.equals(ehrAccess, ehr.ehrAccess) &&
                Objects.equals(compositions, ehr.compositions) &&
                Objects.equals(directory, ehr.directory) &&
                Objects.equals(timeCreated, ehr.timeCreated) &&
                Objects.equals(folders, ehr.folders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemId, ehrId, contributions, ehrStatus, ehrAccess, compositions, directory, timeCreated, folders);
    }

    @Invariant("Contributions valid")
    public boolean contributionsValid() {
        return InvariantUtil.objectRefTypeEquals(contributions, "CONTRIBUTION");
    }

    @Invariant("Ehr_access_valid")
    public boolean ehrAccessValid() {
        return InvariantUtil.objectRefTypeEquals(ehrAccess, "VERSIONED_EHR_ACCESS");

    }

    @Invariant("Ehr_status_valid")
    public boolean ehrStatusValid() {
        return InvariantUtil.objectRefTypeEquals(ehrStatus, "VERSIONED_EHR_STATUS");

    }

    @Invariant("Compositions_valid")
    public boolean compositionsValid() {
        return InvariantUtil.objectRefTypeEquals(compositions, "VERSIONED_COMPOSITION");

    }

    @Invariant("Directory_valid")
    public boolean directoryValid() {
        return InvariantUtil.objectRefTypeEquals(directory, "VERSIONED_FOLDER");

    }


    @Invariant("Folderss_valid")
    public boolean foldersValid() {
        return InvariantUtil.objectRefTypeEquals(folders, "VERSIONED_FOLDER");

    }

    @Invariant("Directory_in_folders")
    public boolean directoryInFolders() {
        if (folders != null && directory != null) {
            return folders.size() >= 1 && folders.get(0).equals(directory);
        }
        return true;
    }

}
