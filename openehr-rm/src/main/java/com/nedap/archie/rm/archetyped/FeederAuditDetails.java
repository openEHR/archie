package com.nedap.archie.rm.archetyped;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.rm.generic.PartyProxy;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FEEDER_AUDIT_DETAILS")
public class FeederAuditDetails extends RMObject {

    @XmlElement(name = "system_id")
    protected String systemId;
    @Nullable
    protected PartyIdentified location;
    @Nullable
    protected PartyIdentified provider;
    @Nullable
    protected PartyProxy subject;
    @Nullable
    protected DvDateTime time;
    @Nullable
    @XmlElement(name = "version_id")
    protected String versionId;

    public FeederAuditDetails() {
    }

    public FeederAuditDetails(String systemId, @Nullable PartyIdentified provider, @Nullable PartyIdentified location, @Nullable DvDateTime time, @Nullable PartyProxy subject, @Nullable String versionId) {
        this.systemId = systemId;
        this.location = location;
        this.provider = provider;
        this.subject = subject;
        this.time = time;
        this.versionId = versionId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public PartyIdentified getLocation() {
        return location;
    }

    public void setLocation(PartyIdentified location) {
        this.location = location;
    }

    public PartyIdentified getProvider() {
        return provider;
    }

    public void setProvider(PartyIdentified provider) {
        this.provider = provider;
    }

    public PartyProxy getSubject() {
        return subject;
    }

    public void setSubject(PartyProxy subject) {
        this.subject = subject;
    }

    public DvDateTime getTime() {
        return time;
    }

    public void setTime(DvDateTime time) {
        this.time = time;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
}
