package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeConstraint;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.query.APathQuery;

import java.util.ArrayList;
import java.util.List;

public class DummyRulesPrimitiveObjectParent extends CAttribute {
    private final transient Archetype archetype;
    private String path;

    public DummyRulesPrimitiveObjectParent() {
        super();
        this.archetype = null;
        this.path = "/";
    }

    public DummyRulesPrimitiveObjectParent(Archetype archetype) {
        super();
        this.archetype = archetype;
        this.path = "/";
    }

    public DummyRulesPrimitiveObjectParent(Archetype archetype, String path) {
        super();
        this.archetype = archetype;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public List<PathSegment> getPathSegments() {
        return new APathQuery(path).getPathSegments();
    }

    @Override
    public String getLogicalPath() {
        return path;
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
