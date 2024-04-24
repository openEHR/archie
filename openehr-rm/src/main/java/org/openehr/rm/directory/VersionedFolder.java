package org.openehr.rm.directory;

import org.openehr.rm.changecontrol.VersionedObject;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;

import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlType(name="VERSIONED_FOLDER")
public class VersionedFolder extends VersionedObject<Folder> {

    public VersionedFolder() {
    }

    public VersionedFolder(HierObjectId uid, ObjectRef<? extends ObjectId> ownerId, DvDateTime timeCreated) {
        super(uid, ownerId, timeCreated);
    }
}
