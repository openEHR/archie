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
    // An explicit archetype ref from a C_ARCHETYPE_ROOT (use archetype...). null otherwise
    private String archetypeRef;
    private Integer index;

    public PathSegment() {
        this(null, null, null, null);
    }

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
        this(nodeName, nodeId, null, index);
    }

    public PathSegment(String nodeName, String nodeId, String archetypeRef, Integer index) {
        this.nodeName = nodeName;
        this.nodeId = nodeId;
        this.archetypeRef = archetypeRef;
        this.index = index;
    }

    public String getNodeName() {
        return nodeName;
    }

    /**
     * @deprecated This object will become immuatable. Use {@link #withNodeName(String)} or a constructor instead.
     */
    @Deprecated
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * Creates a new PathSegment with the given nodeName.
     */
    public PathSegment withNodeName(String nodeName) {
        return new PathSegment(nodeName, this.nodeId, this.archetypeRef, this.index);
    }

    public String getNodeId() {
        return nodeId;
    }

    /**
     * @deprecated This object will become immuatable. Use {@link #withNodeId(String)} or a constructor instead.
     */
    @Deprecated
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Creates a new PathSegment with the given nodeId.
     */
    public PathSegment withNodeId(String nodeId) {
        return new PathSegment(this.nodeName, nodeId, this.archetypeRef, this.index);
    }

    public Integer getIndex() {
        return index;
    }

    /**
     * @deprecated This object will become immuatable. Use {@link #withIndex(Integer)} or a constructor instead.
     */
    @Deprecated
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Creates a new PathSegment with the given index.
     */
    public PathSegment withIndex(Integer index) {
        return new PathSegment(this.nodeName, this.nodeId, this.archetypeRef, index);
    }

    public String getArchetypeRef() {
        return archetypeRef;
    }

    /**
     * @deprecated This object will become immuatable. Use {@link #withArchetypeRef(String)} or a constructor instead.
     */
    @Deprecated
    public void setArchetypeRef(String archetypeRef) {
        this.archetypeRef = archetypeRef;
    }

    /**
     * Creates a new PathSegment with the given archetypeRef.
     */
    public PathSegment withArchetypeRef(String archetypeRef) {
        return new PathSegment(this.nodeName, this.nodeId, archetypeRef, this.index);
    }

    public boolean hasIdCode() {
        return nodeId != null && nodeIdPattern.matcher(nodeId).matches();
    }

    public boolean hasNumberIndex() { return index != null;}

    public boolean hasArchetypeRef() {
        return nodeId != null && archetypeRefPattern.matcher(nodeId).matches();
    }

    @Override
    public String toString() {
        if(hasExpressions()) {
            return "/" + nodeName + "[" +  expressionJoiner.join(nodeId, index) + "]";
        } else {
            return "/" + nodeName;
        }
    }

    public boolean hasExpressions() {
        return nodeId != null || index != null;
    }
}
