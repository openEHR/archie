package com.nedap.archie.rm.generic;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvEHRURI;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.encapsulated.DvMultimedia;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rminfo.RMProperty;

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
@XmlType(name = "ATTESTATION")
public class Attestation extends AuditDetails {

    @Nullable
    @XmlElement(name = "attested_view")
    private DvMultimedia attestedView;
    @Nullable
    private String proof;
    @Nullable
    private List<DvEHRURI> items = new ArrayList<>();
    private DvText reason;
    @XmlElement(name = "is_pending")
    @RMProperty("is_pending")
    private boolean isPending;

    public Attestation() {
    }

    public Attestation(String systemId, PartyProxy committer, DvDateTime timeCommitted, DvCodedText changeType, @Nullable DvText description, @Nullable DvMultimedia attestedView, @Nullable String proof, @Nullable List<DvEHRURI> items, DvText reason, boolean isPending) {
        super(systemId, committer, timeCommitted, changeType, description);
        this.attestedView = attestedView;
        this.proof = proof;
        this.items = items;
        this.reason = reason;
        this.isPending = isPending;
    }

    @Nullable
    public DvMultimedia getAttestedView() {
        return attestedView;
    }

    public void setAttestedView(@Nullable DvMultimedia attestedView) {
        this.attestedView = attestedView;
    }

    @Nullable
    public String getProof() {
        return proof;
    }

    public void setProof(@Nullable String proof) {
        this.proof = proof;
    }

    @Nullable
    public List<DvEHRURI> getItems() {
        return items;
    }

    public void setItems(@Nullable List<DvEHRURI> items) {
        this.items = items;
    }

    public DvText getReason() {
        return reason;
    }

    public void setReason(DvText reason) {
        this.reason = reason;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Attestation that = (Attestation) o;

        if (isPending != that.isPending) return false;
        if (attestedView != null ? !attestedView.equals(that.attestedView) : that.attestedView != null) return false;
        if (proof != null ? !proof.equals(that.proof) : that.proof != null) return false;
        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        return reason != null ? reason.equals(that.reason) : that.reason == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (attestedView != null ? attestedView.hashCode() : 0);
        result = 31 * result + (proof != null ? proof.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (isPending ? 1 : 0);
        return result;
    }
}
