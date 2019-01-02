package com.nedap.archie.rm.changecontrol;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.support.identification.ObjectVersionId;

/**
 * Created by pieter.bos on 08/07/16.
 */
public class ImportedVersion extends Version {

    private OriginalVersion item;

    @Override
    public ObjectVersionId getUid() {
        return item == null ? null : item.getUid();
    }

    @Override
    public ObjectVersionId getPrecedingVersionUid() {
        return item == null ? null : item.getPrecedingVersionUid();
    }

    @Override
    public Object getData() {
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

    public OriginalVersion getItem() {
        return item;
    }
}
