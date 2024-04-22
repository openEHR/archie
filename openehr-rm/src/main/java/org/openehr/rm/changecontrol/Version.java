package org.openehr.rm.changecontrol;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.generic.AuditDetails;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.ObjectVersionId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rminfo.RMProperty;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Version class. You will need to create a subclass to make this work.
 * <p>
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VERSION", propOrder = {
        "contribution",
        "commitAudit",
        "signature"

})
public abstract class Version<Type> extends RMObject {
    private ObjectRef<? extends ObjectId> contribution;
    @Nullable

    private String signature;
    @XmlElement(name = "commit_audit")
    private AuditDetails commitAudit;

    public Version() {
    }

    public Version(AuditDetails commitAudit, ObjectRef<? extends ObjectId> contribution, @Nullable String signature) {
        this.contribution = contribution;
        this.signature = signature;
        this.commitAudit = commitAudit;
    }

    public ObjectRef<? extends ObjectId> getContribution() {
        return contribution;
    }

    public void setContribution(ObjectRef<? extends ObjectId> contribution) {
        this.contribution = contribution;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public AuditDetails getCommitAudit() {
        return commitAudit;
    }

    public void setCommitAudit(AuditDetails commitAudit) {
        this.commitAudit = commitAudit;
    }

    public abstract ObjectVersionId getUid();

    public abstract ObjectVersionId getPrecedingVersionUid();

    public abstract Type getData();

    public abstract DvCodedText getLifecycleState();

    public abstract String getCanonicalForm();

//    public HierObjectId getOwnerId() {
//        if(getUid() != null) {
//            return getUid().getObjectId().getValue();
//        }
//
//    }

    @RMProperty("is_branch")
    public abstract boolean isBranch();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version<?> version = (Version<?>) o;
        return Objects.equals(contribution, version.contribution) &&
                Objects.equals(signature, version.signature) &&
                Objects.equals(commitAudit, version.commitAudit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contribution, signature, commitAudit);
    }

    //TODO: Preceding_version_uid_validity, if version_tree_id() is implemented

    @Invariant("Lifecycle_state_valid")
    public boolean lifecycleStateValid() {
        return InvariantUtil.belongsToTerminologyByGroupId(getLifecycleState(), "version lifecycle state");
    }
}
