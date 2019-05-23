package com.nedap.archie.rm.changecontrol;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.generic.AuditDetails;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlType(name = "CONTRIBUTION", propOrder = {
        "uid",
        "versions",
        "audit"
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Contribution extends RMObject {

    private HierObjectId uid;
    private List<ObjectRef> versions = new ArrayList<>();
    private AuditDetails audit;

    public Contribution() {
    }

    public Contribution(HierObjectId uid, List<ObjectRef> versions, AuditDetails audit) {
        this.uid = uid;
        this.versions = versions;
        this.audit = audit;
    }

    public HierObjectId getUid() {
        return uid;
    }

    public void setUid(HierObjectId uid) {
        this.uid = uid;
    }

    public List<ObjectRef> getVersions() {
        return versions;
    }

    public void setVersions(List<ObjectRef> versions) {
        this.versions = versions;
    }

    public AuditDetails getAudit() {
        return audit;
    }

    public void setAudit(AuditDetails audit) {
        this.audit = audit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contribution that = (Contribution) o;

        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        if (versions != null ? !versions.equals(that.versions) : that.versions != null) return false;
        return audit != null ? audit.equals(that.audit) : that.audit == null;

    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (versions != null ? versions.hashCode() : 0);
        result = 31 * result + (audit != null ? audit.hashCode() : 0);
        return result;
    }
}
