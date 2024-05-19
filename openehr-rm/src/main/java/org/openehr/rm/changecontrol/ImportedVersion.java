package org.openehr.rm.changecontrol;

import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.generic.AuditDetails;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.ObjectVersionId;
import com.nedap.archie.rminfo.RMProperty;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlType(name = "IMPORTED_VERSION")
public class ImportedVersion<T> extends Version<T> {

    private OriginalVersion<T> item;

    public ImportedVersion() {
    }

    public ImportedVersion(AuditDetails commitAudit, ObjectRef<? extends ObjectId> contribution, @Nullable String signature, OriginalVersion<T> item) {
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
    @RMProperty("is_branch")
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

        return Objects.equals(item, that.item);

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), item);
    }
}
