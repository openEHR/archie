package org.openehr.rm.archetyped;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.datavalues.DvIdentifier;
import org.openehr.rm.datavalues.encapsulated.DvEncapsulated;

import javax.annotation.Nullable;
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

        return Objects.equals(originatingSystemItemIds, that.originatingSystemItemIds) &&
                Objects.equals(feederSystemItemIds, that.feederSystemItemIds) &&
                Objects.equals(originalContent, that.originalContent) &&
                Objects.equals(originatingSystemAudit, that.originatingSystemAudit) &&
                Objects.equals(feederSystemAudit, that.feederSystemAudit);

    }

    @Override
    public int hashCode() {
        return Objects.hash(originatingSystemItemIds, feederSystemItemIds, originalContent, originatingSystemAudit, feederSystemAudit);
    }
}
