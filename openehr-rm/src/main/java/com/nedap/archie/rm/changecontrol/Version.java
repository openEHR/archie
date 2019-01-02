package com.nedap.archie.rm.changecontrol;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.generic.AuditDetails;
import com.nedap.archie.rm.support.identification.ObjectVersionId;

import javax.annotation.Nullable;

/**
 * Version class. You will need to create a subclass to make this work.
 *
 * Created by pieter.bos on 08/07/16.
 */
public abstract class Version<Type> extends RMObject {
    private ObjectRef contribution;
    @Nullable
    private String signature;
    private AuditDetails commitAudit;

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

    public abstract boolean isBranch();


}
