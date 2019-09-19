package com.nedap.archie.rm.changecontrol;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.generic.AuditDetails;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.ObjectVersionId;
import com.nedap.archie.rminfo.RMProperty;

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
    private ObjectRef contribution;
    @Nullable

    private String signature;
    @XmlElement(name = "commit_audit")
    private AuditDetails commitAudit;

    public Version() {
    }

    public Version(AuditDetails commitAudit, ObjectRef contribution, @Nullable String signature) {
        this.contribution = contribution;
        this.signature = signature;
        this.commitAudit = commitAudit;
    }

    public ObjectRef getContribution() {
        return contribution;
    }

    public void setContribution(ObjectRef contribution) {
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
}
