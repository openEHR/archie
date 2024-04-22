package org.openehr.rm.ehr;

import org.openehr.rm.changecontrol.VersionedObject;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

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
