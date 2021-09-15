package com.nedap.archie.query;

import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.Objects;
import java.util.function.Predicate;

class NodeIdPredicate implements Predicate<Object> {
    private final ModelInfoLookup lookup;
    private final String nodeId;

    public NodeIdPredicate(ModelInfoLookup lookup, String nodeId) {
        this.lookup = lookup;
        this.nodeId = nodeId;
    }

    @Override
    public boolean test(Object o) {
        if(o == null) {
            return false;
        }
        String archetypeNodeIdFromRMObject = lookup.getArchetypeNodeIdFromRMObject(o);

        return Objects.equals(archetypeNodeIdFromRMObject, nodeId);//TODO: also match specialized nodes!
    }
}
