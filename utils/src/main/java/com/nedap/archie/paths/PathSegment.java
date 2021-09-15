package com.nedap.archie.paths;

import com.google.common.base.Joiner;

import java.util.regex.Pattern;

/**
 * Segment of an apath-query
 * Created by pieter.bos on 19/10/15.
 */
public class PathSegment {
    private final static Joiner expressionJoiner = Joiner.on(",").skipNulls();

    private static final Pattern archetypeRefPattern = Pattern.compile("(.*::)?.*-.*-.*\\..*\\.v.*");
    private static final Pattern nodeIdPattern = Pattern.compile("id(\\.?\\d)+|at(\\.?\\d)+");

    private String nodeName;
    private String nodeId;
    private String objectNameConstraint;
    // An explicit archetype ref from a C_ARCHETYPE_ROOT (use archetype...). null otherwise
    private String archetypeRef = null;
    private Integer index;

    public PathSegment(String nodeName, Integer index) {
        this(nodeName, null, index);
    }

    public PathSegment(String nodeName, String nodeId) {
        this(nodeName, nodeId, null);
    }

    public PathSegment(String nodeName) {
        this(nodeName, null, null);
    }

    public PathSegment(String nodeName, String nodeId, Integer index) {
        this.nodeName = nodeName;
        this.nodeId = nodeId;
        this.index = index;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getArchetypeRef() {
        return archetypeRef;
    }

    public void setArchetypeRef(String archetypeRef) {
        this.archetypeRef = archetypeRef;
    }

    public String getObjectNameConstraint() {
        return objectNameConstraint;
    }

    public void setObjectNameConstraint(String objectNameConstraint) {
        this.objectNameConstraint = objectNameConstraint;
    }

    public boolean hasIdCode() {
        return nodeId != null && isIdCode(nodeId);
    }

    public boolean hasNumberIndex() { return index != null;}

    public static boolean isIdCode(String code) {
        return nodeIdPattern.matcher(code).matches();
    }

    public static boolean isArchetypeRef(String code) {
        return archetypeRefPattern.matcher(code).matches();
    }

    public boolean hasArchetypeRef() {
        return nodeId != null && isArchetypeRef(nodeId);
    }

    @Override
    public String toString() {
        if(hasExpressions()) {
            if(objectNameConstraint != null && nodeId != null && !nodeId.equals("id9999")) {
                return "/" + nodeName + "[" + expressionJoiner.join(nodeId, index) + " and name/value='" + objectNameConstraint + "']";
            } else if(objectNameConstraint == null && nodeId != null && !nodeId.equals("id9999")){
                return "/" + nodeName + "[" + expressionJoiner.join(nodeId, index) + "]";
            } else if (nodeId == null || !nodeId.equals("id9999")) {
                return "/" + nodeName + "[" + expressionJoiner.join(objectNameConstraint, index) + "]";
            } else if(index != null) {
                return "/" + nodeName + "[" + index + "]";
            }
        }
        return "/" + nodeName;
    }

    public boolean hasExpressions() {
        return nodeId != null || index != null || objectNameConstraint != null;
    }

    public boolean hasObjectNameConstraint() {
        return objectNameConstraint != null;
    }
}
