package com.nedap.archie.query;

import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.function.Predicate;

/** converts the nodeid/nodename/position part of a path segment to a java Predicate to tests
 **/

public class RmPathQueryPredicateConverter {
    public static Predicate<Object> convert(PathSegment segment, ModelInfoLookup lookup) {
        Predicate<Object> result = o -> true;
        if(segment.hasIdCode()) {
            result = result.and(new NodeIdPredicate(lookup, segment.getNodeId()));
        }
        if(segment.hasArchetypeRef()) {
            result = result.and(new ArchetypeRefPredicate(lookup, segment.getNodeId()));
        }
        if(segment.getArchetypeRef() != null) { //TODO: why is this a different case - solve this!
            result = result.and(new ArchetypeRefPredicate(lookup, segment.getArchetypeRef()));
        }
        if(segment.hasObjectNameConstraint()) {
            result = result.and(new NodeNamePredicate(lookup, segment.getObjectNameConstraint()));
        }
        return result;
    }

    public static Predicate<Object> convertWithoutNodeId(PathSegment segment, ModelInfoLookup lookup) {
        Predicate<Object> result = o -> true;
        if(segment.hasIdCode()) {
            result = result.and(new NoNodeIdPredicate(lookup, segment.getNodeId()));
        }
        if(segment.hasArchetypeRef()) {
            result = result.and(new ArchetypeRefPredicate(lookup, segment.getNodeId()));
        }
        if(segment.getArchetypeRef() != null) { //TODO: why is this a different case - solve this!
            result = result.and(new ArchetypeRefPredicate(lookup, segment.getArchetypeRef()));
        }
        if(segment.hasObjectNameConstraint()) {
            result = result.and(new NodeNamePredicate(lookup, segment.getObjectNameConstraint()));
        }
        return result;
    }
}
