package org.openehr.rm.generic;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.support.identification.ObjectVersionId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return Objects.equals(versionId, that.versionId) &&
                Objects.equals(audits, that.audits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionId, audits);
    }
}
