package com.nedap.archie.rm.ehr;

import com.nedap.archie.rm.changecontrol.VersionedObject;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="VERSIONED_EHR_ACCESS")
public class VersionedEhrAccess extends VersionedObject<EhrAccess> {

    public VersionedEhrAccess() {
    }

    public VersionedEhrAccess(HierObjectId uid, ObjectRef ownerId, DvDateTime timeCreated) {
        super(uid, ownerId, timeCreated);
    }
}
