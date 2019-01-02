package com.nedap.archie.rm.generic;

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
@XmlType(name="AUDIT_DETAILS")
public class AuditDetails extends RMObject {

    private String systemId;
    @XmlElement(name="time_committed")
    private DvDateTime timeCommitted;
    private DvCodedText changeType;
    @Nullable
    private DvText description;
    private PartyProxy committer;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public DvDateTime getTimeCommitted() {
        return timeCommitted;
    }

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
}
