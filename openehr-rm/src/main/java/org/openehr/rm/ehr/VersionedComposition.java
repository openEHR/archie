package org.openehr.rm.ehr;

import org.openehr.rm.changecontrol.VersionedObject;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;

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
