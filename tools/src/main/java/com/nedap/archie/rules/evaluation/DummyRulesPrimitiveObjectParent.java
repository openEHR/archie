package com.nedap.archie.rules.evaluation;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.apath.PathSegment;

import java.util.ArrayList;
import java.util.List;

public class DummyRulesPrimitiveObjectParent extends CAttribute {
    private final transient Archetype archetype;
    private List<PathSegment> pathSegments;

    public DummyRulesPrimitiveObjectParent(Archetype archetype) {
        super();
        this.archetype = archetype;
        this.pathSegments = new ArrayList<>();
    }

    public void setPathSegments(List<PathSegment> pathSegments) {
        this.pathSegments = pathSegments;
    }

    @Override
    public List<PathSegment> getPathSegments() {
       return pathSegments;
    }

    @Override
    public String getLogicalPath() {
        return getPath();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Archetype getArchetype() {
        return archetype;
    }


}
