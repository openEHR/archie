package com.nedap.archie.rm.ehr;

import com.nedap.archie.rm.changecontrol.VersionedObject;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VERSIONED_EHR_STATUS")
public class VersionedEhrStatus extends VersionedObject<EhrStatus> {

    public VersionedEhrStatus() {
    }

    public VersionedEhrStatus(HierObjectId uid, ObjectRef<? extends ObjectId> ownerId, DvDateTime timeCreated) {
        super(uid, ownerId, timeCreated);
    }
}
