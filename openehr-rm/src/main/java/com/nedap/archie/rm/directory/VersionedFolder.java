package com.nedap.archie.rm.directory;

import com.nedap.archie.rm.changecontrol.VersionedObject;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;

import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlType(name="VERSIONED_FOLDER")
public class VersionedFolder extends VersionedObject<Folder> {

    public VersionedFolder() {
    }

    public VersionedFolder(HierObjectId uid, ObjectRef ownerId, DvDateTime timeCreated) {
        super(uid, ownerId, timeCreated);
    }
}
