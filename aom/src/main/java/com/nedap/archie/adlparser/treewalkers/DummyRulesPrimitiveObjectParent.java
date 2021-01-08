package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeConstraint;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.paths.PathSegment;

import java.util.ArrayList;
import java.util.List;

public class DummyRulesPrimitiveObjectParent extends CAttribute {
    private final transient Archetype archetype;

    public DummyRulesPrimitiveObjectParent() {
        super();
        this.archetype = null;
    }

    public DummyRulesPrimitiveObjectParent(Archetype archetype) {
        super();
        this.archetype = archetype;
    }

    @Override
    public List<PathSegment> getPathSegments() {
        return new ArrayList<>();
    }

    @Override
    public String getLogicalPath() {
        return "/";
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
