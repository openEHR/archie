package com.nedap.archie.rm.generic;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.support.identification.ObjectVersionId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "REVISION_HISTORY_ITEM")
public class RevisionHistoryItem extends RMObject {

    @XmlElement(name = "version_id")
    private ObjectVersionId versionId;
    private List<AuditDetails> audits = new ArrayList<>();

    public RevisionHistoryItem() {
    }

    public RevisionHistoryItem(ObjectVersionId versionId, List<AuditDetails> audits) {
        this.versionId = versionId;
        this.audits = audits;
    }

    public ObjectVersionId getVersionId() {
        return versionId;
    }

    public void setVersionId(ObjectVersionId versionId) {
        this.versionId = versionId;
    }

    public List<AuditDetails> getAudits() {
        return audits;
    }

    public void setAudits(List<AuditDetails> audits) {
        this.audits = audits;
    }

    public void addAudit(AuditDetails audit) {
        this.audits.add(audit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionHistoryItem that = (RevisionHistoryItem) o;

        if (versionId != null ? !versionId.equals(that.versionId) : that.versionId != null) return false;
        return audits != null ? audits.equals(that.audits) : that.audits == null;

    }

    @Override
    public int hashCode() {
        int result = versionId != null ? versionId.hashCode() : 0;
        result = 31 * result + (audits != null ? audits.hashCode() : 0);
        return result;
    }
}
