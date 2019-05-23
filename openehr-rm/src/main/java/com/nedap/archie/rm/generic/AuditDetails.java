package com.nedap.archie.rm.generic;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AUDIT_DETAILS", propOrder = {
        "systemId",
        "committer",
        "timeCommitted",
        "changeType",
        "description"
})
public class AuditDetails extends RMObject {

    @XmlElement(name = "system_id")
    private String systemId;
    @XmlElement(name = "time_committed")
    private DvDateTime timeCommitted;
    @XmlElement(name = "change_type")
    private DvCodedText changeType;
    @Nullable
    private DvText description;
    private PartyProxy committer;

    public AuditDetails() {
    }


    public AuditDetails(String systemId, PartyProxy committer, DvDateTime timeCommitted, DvCodedText changeType, @Nullable DvText description) {
        this.systemId = systemId;
        this.timeCommitted = timeCommitted;
        this.changeType = changeType;
        this.description = description;
        this.committer = committer;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public DvDateTime getTimeCommitted() {
        return timeCommitted;
    }

    @JsonAlias({"time_commited"})
    public void setTimeCommitted(DvDateTime timeCommitted) {
        this.timeCommitted = timeCommitted;
    }

    public DvCodedText getChangeType() {
        return changeType;
    }

    public void setChangeType(DvCodedText changeType) {
        this.changeType = changeType;
    }

    @Nullable
    public DvText getDescription() {
        return description;
    }

    public void setDescription(@Nullable DvText description) {
        this.description = description;
    }

    public PartyProxy getCommitter() {
        return committer;
    }

    public void setCommitter(PartyProxy committer) {
        this.committer = committer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditDetails that = (AuditDetails) o;

        if (systemId != null ? !systemId.equals(that.systemId) : that.systemId != null) return false;
        if (timeCommitted != null ? !timeCommitted.equals(that.timeCommitted) : that.timeCommitted != null)
            return false;
        if (changeType != null ? !changeType.equals(that.changeType) : that.changeType != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return committer != null ? committer.equals(that.committer) : that.committer == null;

    }

    @Override
    public int hashCode() {
        int result = systemId != null ? systemId.hashCode() : 0;
        result = 31 * result + (timeCommitted != null ? timeCommitted.hashCode() : 0);
        result = 31 * result + (changeType != null ? changeType.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (committer != null ? committer.hashCode() : 0);
        return result;
    }
}
