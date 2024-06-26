package com.nedap.archie.rm.ehr;

import com.nedap.archie.rm.changecontrol.VersionedObject;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;

/**
 * Created by pieter.bos on 08/07/16.
 */
public class VersionedComposition extends VersionedObject<Composition> {

    public VersionedComposition() {
    }

    public VersionedComposition(HierObjectId uid, ObjectRef<? extends ObjectId> ownerId, DvDateTime timeCreated) {
        super(uid, ownerId, timeCreated);
    }

   //TODO: invariants maybe not so relevant here, since the implementation is CDR-specific?
}
