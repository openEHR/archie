package org.openehr.rm.datastructures;

import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.UIDBasedId;
import org.openehr.rm.archetyped.*;

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
