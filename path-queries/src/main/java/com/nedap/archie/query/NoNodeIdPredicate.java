package com.nedap.archie.query;

import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Special predicate that checks for a non-existing node id
 * needed when people fire archetype paths at RM instances, where the RM instance does not have a node id, but the
 * archetype does!
 */
class NoNodeIdPredicate implements Predicate<Object> {
    private final ModelInfoLookup lookup;
    private final String nodeId;

    public NoNodeIdPredicate(ModelInfoLookup lookup, String nodeId) {//constructor nodeid parameter is there only to indicate it must be present
        this.lookup = lookup;
        this.nodeId = nodeId;
    }

    @Override
    public boolean test(Object o) {
        if(o == null) {
            return false;
        }
        String archetypeNodeIdFromRMObject = lookup.getArchetypeNodeIdFromRMObject(o);
        //the null is because sometimes things in archetypes have node ids, and in rm objects they do not!
        return archetypeNodeIdFromRMObject == null;
    }
}