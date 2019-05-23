package com.nedap.archie.rm.archetyped;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.datavalues.encapsulated.DvEncapsulated;

import javax.annotation.Nullable;
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
@XmlType(name = "FEEDER_AUDIT")
public class FeederAudit extends RMObject {

    @Nullable
    @XmlElement(name = "originating_system_item_ids")
    protected List<DvIdentifier> originatingSystemItemIds = new ArrayList<>();
    @Nullable
    @XmlElement(name = "feeder_system_item_ids")
    protected List<DvIdentifier> feederSystemItemIds = new ArrayList<>();
    @Nullable
    @XmlElement(name = "original_content")
    protected DvEncapsulated originalContent;

    @XmlElement(name = "originating_system_audit")
    protected FeederAuditDetails originatingSystemAudit;
    @Nullable
    @XmlElement(name = "feeder_system_audit")
    protected FeederAuditDetails feederSystemAudit;

    public FeederAudit() {
    }

    public FeederAudit(FeederAuditDetails originatingSystemAudit, @Nullable List<DvIdentifier> originatingSystemItemIds, @Nullable FeederAuditDetails feederSystemAudit, @Nullable List<DvIdentifier> feederSystemItemIds, @Nullable DvEncapsulated originalContent) {
        this.originatingSystemItemIds = originatingSystemItemIds;
        this.feederSystemItemIds = feederSystemItemIds;
        this.originalContent = originalContent;
        this.originatingSystemAudit = originatingSystemAudit;
        this.feederSystemAudit = feederSystemAudit;
    }

    public List<DvIdentifier> getOriginatingSystemItemIds() {
        return originatingSystemItemIds;
    }

    public void setOriginatingSystemItemIds(List<DvIdentifier> originatingSystemItemIds) {
        this.originatingSystemItemIds = originatingSystemItemIds;
    }

    public List<DvIdentifier> getFeederSystemItemIds() {
        return feederSystemItemIds;
    }

    public void setFeederSystemItemIds(List<DvIdentifier> feederSystemItemIds) {
        this.feederSystemItemIds = feederSystemItemIds;
    }

    public DvEncapsulated getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(DvEncapsulated originalContent) {
        this.originalContent = originalContent;
    }

    public FeederAuditDetails getOriginatingSystemAudit() {
        return originatingSystemAudit;
    }

    public void setOriginatingSystemAudit(FeederAuditDetails originatingSystemAudit) {
        this.originatingSystemAudit = originatingSystemAudit;
    }

    public FeederAuditDetails getFeederSystemAudit() {
        return feederSystemAudit;
    }

    public void setFeederSystemAudit(FeederAuditDetails feederSystemAudit) {
        this.feederSystemAudit = feederSystemAudit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeederAudit that = (FeederAudit) o;

        if (originatingSystemItemIds != null ? !originatingSystemItemIds.equals(that.originatingSystemItemIds) : that.originatingSystemItemIds != null)
            return false;
        if (feederSystemItemIds != null ? !feederSystemItemIds.equals(that.feederSystemItemIds) : that.feederSystemItemIds != null)
            return false;
        if (originalContent != null ? !originalContent.equals(that.originalContent) : that.originalContent != null)
            return false;
        if (originatingSystemAudit != null ? !originatingSystemAudit.equals(that.originatingSystemAudit) : that.originatingSystemAudit != null)
            return false;
        return feederSystemAudit != null ? feederSystemAudit.equals(that.feederSystemAudit) : that.feederSystemAudit == null;

    }

    @Override
    public int hashCode() {
        int result = originatingSystemItemIds != null ? originatingSystemItemIds.hashCode() : 0;
        result = 31 * result + (feederSystemItemIds != null ? feederSystemItemIds.hashCode() : 0);
        result = 31 * result + (originalContent != null ? originalContent.hashCode() : 0);
        result = 31 * result + (originatingSystemAudit != null ? originatingSystemAudit.hashCode() : 0);
        result = 31 * result + (feederSystemAudit != null ? feederSystemAudit.hashCode() : 0);
        return result;
    }
}
