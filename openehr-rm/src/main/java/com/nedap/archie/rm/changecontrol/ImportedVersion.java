package com.nedap.archie.rm.changecontrol;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.generic.AuditDetails;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.ObjectVersionId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlType(name = "IMPORTED_VERSION")
public class ImportedVersion<T> extends Version<T> {

    private OriginalVersion<T> item;

    public ImportedVersion() {
    }

    public ImportedVersion(AuditDetails commitAudit, ObjectRef contribution, @Nullable String signature, OriginalVersion item) {
        super(commitAudit, contribution, signature);
        this.item = item;
    }

    @Override
    public ObjectVersionId getUid() {
        return item == null ? null : item.getUid();
    }

    @Override
    public ObjectVersionId getPrecedingVersionUid() {
        return item == null ? null : item.getPrecedingVersionUid();
    }

    @Override
    public T getData() {
        return item == null ? null : item.getData();
    }

    @Override
    public DvCodedText getLifecycleState() {
        return item == null ? null : item.getLifecycleState();
    }

    @Override
    public String getCanonicalForm() {
        return item == null ? null : item.getCanonicalForm();//TODO: this is probably not right
    }

    @Override
    public boolean isBranch() {
        return item == null ? null : item.isBranch();//TODO: this is probably not right
    }

    public OriginalVersion<T> getItem() {
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ImportedVersion<?> that = (ImportedVersion<?>) o;

        return item != null ? item.equals(that.item) : that.item == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }
}
