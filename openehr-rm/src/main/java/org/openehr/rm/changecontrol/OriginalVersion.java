package org.openehr.rm.changecontrol;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.generic.Attestation;
import org.openehr.rm.generic.AuditDetails;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.ObjectVersionId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rminfo.RMProperty;
import com.nedap.archie.rminfo.RMPropertyIgnore;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlType(name = "ORIGINAL_VERSION", propOrder = {
        "uid",
        "data",
        "precedingVersionUid",
        "otherInputVersionUids",
        "attestations",
        "lifecycleState"

})
public class    OriginalVersion<Type> extends Version<Type> {

    private ObjectVersionId uid;
    @Nullable
    @XmlElement(name = "preceding_version_uid")
    private ObjectVersionId precedingVersionUid;
    @Nullable
    @XmlElement(name = "other_input_version_uids")
    private List<ObjectVersionId> otherInputVersionUids = new ArrayList<>();

    @XmlElement(name = "lifecycle_state")
    private DvCodedText lifecycleState;
    @Nullable
    private List<Attestation> attestations = new ArrayList<>();
    @Nullable
    private Type data;


    public OriginalVersion() {
    }

    public OriginalVersion(ObjectVersionId uid, @Nullable ObjectVersionId precedingVersionUid, @Nullable Type data, DvCodedText lifecycleState, AuditDetails commitAudit, ObjectRef<? extends ObjectId> contribution, @Nullable String signature, @Nullable List<ObjectVersionId> otherInputVersionUids, @Nullable List<Attestation> attestations) {
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


    @JsonIgnore
    @RMPropertyIgnore
    @XmlTransient
    public boolean isMerged() {
        return otherInputVersionUids != null && !otherInputVersionUids.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OriginalVersion<?> that = (OriginalVersion<?>) o;

        return Objects.equals(uid, that.uid) &&
                Objects.equals(precedingVersionUid, that.precedingVersionUid) &&
                Objects.equals(otherInputVersionUids, that.otherInputVersionUids) &&
                Objects.equals(otherInputVersionUids, that.otherInputVersionUids) &&
                Objects.equals(lifecycleState, that.lifecycleState) &&
                Objects.equals(attestations, that.attestations) &&
                Objects.equals(data, that.data);

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uid, precedingVersionUid, otherInputVersionUids, lifecycleState, attestations, data);
    }

    @Invariant(value = "Attestations_valid", ignored = true)
    public boolean attestationsValid() {
        return InvariantUtil.nullOrNotEmpty(attestations);
    }

    @Invariant(value = "Is_merged_validity", ignored = true)
    public boolean mergedValidity() {
        //not data validity, so let's ignore this
        return (otherInputVersionUids == null || otherInputVersionUids.isEmpty()) ^ isMerged();
    }

    @Invariant(value = "Other_input_version_uids_valid", ignored = true)
    public boolean otherInputVersionUidsValid() {
        return InvariantUtil.nullOrNotEmpty(otherInputVersionUids);
    }
}
