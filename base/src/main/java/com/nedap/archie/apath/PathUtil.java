package com.nedap.archie.apath;


import com.nedap.archie.definitions.AdlCodeDefinitions;

import java.util.List;

/**
 * Created by pieter.bos on 07/04/16.
 */
public class PathUtil {

    public static String getPath(List<PathSegment> pathSegments) {
        StringBuilder result = new StringBuilder();

        if(pathSegments.isEmpty()) {
            return "/";
        }
        for(PathSegment segment: pathSegments) {
            result.append("/");
            result.append(segment.getNodeName());
            if(segment.getNodeId() != null && !segment.getNodeId().equals(AdlCodeDefinitions.PRIMITIVE_NODE_ID)) {
                result.append("[");
                result.append(segment.getNodeId());
                if(segment.hasNumberIndex()) {
                    result.append(",");
                    result.append(segment.getIndex().toString());
                }
                result.append("]");
            } else if (segment.hasNumberIndex()) {
                result.append("[");
                result.append(segment.getIndex());
                result.append("]");
            }
        }
        return result.toString();
    }
}
