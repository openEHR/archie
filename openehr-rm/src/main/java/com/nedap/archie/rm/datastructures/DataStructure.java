package com.nedap.archie.rm.datastructures;

import com.nedap.archie.rm.archetyped.*;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
public abstract class DataStructure extends Locatable {

    public DataStructure() {
    }

    public DataStructure(String archetypeNodeId, DvText name) {
        super(archetypeNodeId, name);
    }

    public DataStructure(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
    }
}
