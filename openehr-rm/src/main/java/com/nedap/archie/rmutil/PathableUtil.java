package com.nedap.archie.rmutil;

import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.paths.PathUtil;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.AttributeAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PathableUtil {
    private static final AttributeAccessor attributeAccessor = new AttributeAccessor(ArchieRMInfoLookup.getInstance());

    /**
     * Determine the unique path segments from the toplevel-RM object.
     * <p>
     * Adds index when necessary.
     */
    public static List<PathSegment> getUniquePathSegments(Pathable pathable) {
        Pathable parent = pathable.getParent();
        if (parent == null) {
            return new ArrayList<>();
        }

        List<PathSegment> segments = getUniquePathSegments(parent);

        segments.add(getUniquePathSegment(pathable));
        return segments;
    }

    private static PathSegment getUniquePathSegment(Pathable pathable) {
        List<PathSegment> unindexedPathSegments = pathable.getPathSegments();
        PathSegment unindexedPathSegment = unindexedPathSegments.get(unindexedPathSegments.size() - 1);

        Pathable parent = pathable.getParent();
        String parentAttributeName = unindexedPathSegment.getNodeName();

        Object attributeValue = attributeAccessor.getValue(parent, parentAttributeName);
        Integer index = null;

        if (attributeValue instanceof Collection) {
            Collection<?> collection = (Collection<?>) attributeValue;

            if (collection.size() > 1) {
                int i = 1;
                for (Object elem : collection) {
                    if (elem == pathable) {
                        index = i;
                        break;
                    }
                    i++;
                }

                if (index == null) {
                    throw new RuntimeException("Cannot find object in parent object");
                }
            }
        }

        return new PathSegment(unindexedPathSegment.getNodeName(), unindexedPathSegment.getNodeId(), index);
    }

    /**
     * Determine the unique path from the toplevel-RM object.
     * <p>
     * Adds index when necessary.
     */
    public static String getUniquePath(Pathable pathable) {
        return PathUtil.getPath(getUniquePathSegments(pathable));
    }
}
