package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.paths.PathUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlTransient;

import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 15/10/15.
 */
public abstract class ArchetypeConstraint extends ArchetypeModelObject {

    @JsonIgnore //ignore these field in popular object mappers
    private transient ArchetypeConstraint parent;
    @JsonIgnore //ignore these field in popular object mappers, otherwise we get infinite loops
    @Nullable
    private transient CSecondOrder<?> socParent;

    @JsonIgnore
    @XmlTransient
    public ArchetypeConstraint getParent() {
        return parent;
    }

    public void setParent(ArchetypeConstraint parent) {
        this.parent = parent;
    }

    @JsonIgnore
    @XmlTransient
    public CSecondOrder<?> getSocParent() {
        return socParent;
    }

    public void setSocParent(CSecondOrder<?> socParent) {
        this.socParent = socParent;
    }

    @JsonIgnore
    @XmlTransient
    public abstract List<PathSegment> getPathSegments();

    public final String getPath() {
        return PathUtil.getPath(getPathSegments());
    }

    private void setPath(String path){
        //setter hack for jackson, unfortunately
    }

    public abstract String getLogicalPath();

    private void setLogicalPath(String path){
        //setter hack for jackson, unfortunately
    }

    public String path() {
        return getPath();
    }


    /**
     * True if this node is the root of the tree.
     */
    @JsonIgnore
    public boolean isRoot() {
        return parent == null;
    }

    @JsonIgnore
    public abstract boolean isLeaf();

    @JsonIgnore
    @XmlTransient
    public Archetype getArchetype() {
        ArchetypeConstraint constraint = getParent();
        return constraint == null ? null : constraint.getArchetype();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArchetypeConstraint)) return false;
        ArchetypeConstraint that = (ArchetypeConstraint) o;
        return Objects.equals(parent, that.parent) && Objects.equals(socParent, that.socParent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, socParent);
    }
}
