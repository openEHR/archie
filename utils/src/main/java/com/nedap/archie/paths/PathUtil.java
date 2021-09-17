package com.nedap.archie.paths;


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
           result.append(segment.toString());
        }
        return result.toString();
    }
}
