package org.openehr.rm.generic;

import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.DvEHRURI;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.encapsulated.DvMultimedia;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rminfo.RMProperty;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

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
        return isPending == that.isPending &&
                Objects.equals(attestedView, that.attestedView) &&
                Objects.equals(proof, that.proof) &&
                Objects.equals(items, that.items) &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attestedView, proof, items, reason, isPending);
    }

    @Invariant(value = "Items_valid", ignored = true)
    public boolean itemsValid() {
        return InvariantUtil.nullOrNotEmpty(items);
    }

    @Invariant(value = "Reason_valid", ignored = true)
    public boolean reasonValid() {
        if(reason instanceof DvCodedText) {
            return InvariantUtil.belongsToTerminologyByGroupId((DvCodedText) reason, "attestation reason");
        }
        return true;
    }
}
