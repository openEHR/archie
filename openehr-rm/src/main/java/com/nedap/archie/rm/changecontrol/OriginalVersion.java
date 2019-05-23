package com.nedap.archie.rm.changecontrol;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.generic.Attestation;
import com.nedap.archie.rm.generic.AuditDetails;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.ObjectVersionId;
import com.nedap.archie.rminfo.RMProperty;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlType(name="ORIGINAL_VERSION", propOrder = {
        "uid",
        "data",
        "precedingVersionUid",
        "otherInputVersionUids",
        "attestations",
        "lifecycleState"

})
public class OriginalVersion<Type> extends Version<Type> {

    private ObjectVersionId uid;
    @Nullable
    @XmlElement(name="preceding_version_uid")
    private ObjectVersionId precedingVersionUid;
    @Nullable
    @XmlElement(name="other_input_version_uids")
    private List<ObjectVersionId> otherInputVersionUids = new ArrayList<>();

    @XmlElement(name="lifecycle_state")
    private DvCodedText lifecycleState;
    @Nullable
    private List<Attestation> attestations = new ArrayList<>();
    @Nullable
    private Type data;


    public OriginalVersion() {
    }

    public OriginalVersion(ObjectVersionId uid, @Nullable ObjectVersionId precedingVersionUid, @Nullable Type data, DvCodedText lifecycleState, AuditDetails commitAudit, ObjectRef contribution, @Nullable String signature, @Nullable List<ObjectVersionId> otherInputVersionUids, @Nullable List<Attestation> attestations) {
        super(commitAudit, contribution, signature);
        this.uid = uid;
        this.precedingVersionUid = precedingVersionUid;
        this.otherInputVersionUids = otherInputVersionUids;
        this.lifecycleState = lifecycleState;
        this.attestations = attestations;
        this.data = data;
    }

    @Override
    public ObjectVersionId getUid() {
        return uid;
    }

    @Override
    public ObjectVersionId getPrecedingVersionUid() {
        return precedingVersionUid;
    }

    public void setUid(ObjectVersionId uid) {
        this.uid = uid;
    }

    public void setPrecedingVersionUid(ObjectVersionId precedingVersionUid) {
        this.precedingVersionUid = precedingVersionUid;
    }

    @Nullable
    public List<ObjectVersionId> getOtherInputVersionUids() {
        return otherInputVersionUids;
    }

    @JsonAlias({"other_input_version_ids"})
    public void setOtherInputVersionUids(@Nullable List<ObjectVersionId> otherInputVersionUids) {
        this.otherInputVersionUids = otherInputVersionUids;
    }

    @Override
    public DvCodedText getLifecycleState() {
        return lifecycleState;
    }

    @Override
    public String getCanonicalForm() {
        return null;//TODO no idea what this should do
    }

    @Override
    @RMProperty("is_branch")
    public boolean isBranch() {
        return false;
    }

    public void setLifecycleState(DvCodedText lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public List<Attestation> getAttestations() {
        return attestations;
    }

    public void setAttestations(List<Attestation> attestations) {
        this.attestations = attestations;
    }

    @Override
    public Type getData() {
        return data;
    }

    public void setData(Type data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OriginalVersion<?> that = (OriginalVersion<?>) o;

        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        if (precedingVersionUid != null ? !precedingVersionUid.equals(that.precedingVersionUid) : that.precedingVersionUid != null)
            return false;
        if (otherInputVersionUids != null ? !otherInputVersionUids.equals(that.otherInputVersionUids) : that.otherInputVersionUids != null)
            return false;
        if (lifecycleState != null ? !lifecycleState.equals(that.lifecycleState) : that.lifecycleState != null)
            return false;
        if (attestations != null ? !attestations.equals(that.attestations) : that.attestations != null) return false;
        return data != null ? data.equals(that.data) : that.data == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (precedingVersionUid != null ? precedingVersionUid.hashCode() : 0);
        result = 31 * result + (otherInputVersionUids != null ? otherInputVersionUids.hashCode() : 0);
        result = 31 * result + (lifecycleState != null ? lifecycleState.hashCode() : 0);
        result = 31 * result + (attestations != null ? attestations.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
