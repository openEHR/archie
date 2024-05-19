package org.openehr.rm.archetyped;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.apath.PathUtil;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.base.RMObject;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rminfo.PropertyType;
import com.nedap.archie.rminfo.RMProperty;
import com.nedap.archie.rminfo.RMPropertyIgnore;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PATHABLE")
public abstract class Pathable extends RMObject {
    //TODO: implement according to spec: pathExists(path), pathUnique(path), pathOfItem(pathable)

    @JsonIgnore
    @XmlTransient
    @Nullable
    @RMProperty(value="parent", computed=PropertyType.COMPUTED)
    private Pathable parent;
    @JsonIgnore
    @XmlTransient
    @Nullable
    private String parentAttributeName;

    public Pathable() {
    }

    public Pathable(@Nullable Pathable parent, @Nullable String parentAttributeName) {
        this.parent = parent;
        this.parentAttributeName = parentAttributeName;
    }

    public Object itemAtPath(String s) {
        return new RMPathQuery(s).find(OpenEhrRmInfoLookup.getInstance(), this);
    }

    public Object itemAtPathMatchSpecialisedNodes(String s) {
        return new RMPathQuery(s, true).find(OpenEhrRmInfoLookup.getInstance(), this);
    }

    public List<Object> itemsAtPath(String s) {
        List<RMObjectWithPath> objects = new RMPathQuery(s).findList(OpenEhrRmInfoLookup.getInstance(), this);
        List<Object> result = new ArrayList<>();
        for (RMObjectWithPath object : objects) {
            result.add(object.getObject());
        }
        return result;
    }

    public List<Object> itemsAtPathMatchSpecialisedNodes(String s) {
        List<RMObjectWithPath> objects = new RMPathQuery(s, true).findList(OpenEhrRmInfoLookup.getInstance(), this);
        List<Object> result = new ArrayList<>();
        for (RMObjectWithPath object : objects) {
            result.add(object.getObject());
        }
        return result;
    }

    @JsonIgnore
    public Pathable getParent() {
        return parent;
    }

    private void setParent(Pathable parent) {
        this.parent = parent;
    }

    private void setParentAttributeName(String parentAttributeName) {
        this.parentAttributeName = parentAttributeName;
    }

    /**
     * Utility method to set this object as the parent of the given child,
     * if the child is not null
     */
    protected void setThisAsParent(Pathable child, String attributeName) {
        if (child != null) {
            child.setParent(this);
            child.setParentAttributeName(attributeName);
        }
    }

    /**
     * Utility method to set this object as the parent of the given child,
     * if the child is not null
     */
    protected void setThisAsParent(Collection<? extends Pathable> children, String attributeName) {
        if (children != null) {
            for (Pathable child : children) {
                this.setThisAsParent(child, attributeName);
            }
        }
    }

    @JsonIgnore
    protected String getParentAttributeName() {
        return parentAttributeName;
    }

    @RMPropertyIgnore
    public List<PathSegment> getPathSegments() {
        Pathable parent = getParent();
        if (parent == null) {
            return new ArrayList<>();
        }

        List<PathSegment> segments = parent.getPathSegments();
        segments.add(new PathSegment(parentAttributeName));
        return segments;
    }

    /**
     * Path from the toplevel-RM object. Not sure if this should be here, because the EHR and Folder objects are also in
     * the RM. But for now, it works because the most toplevel element is a Composition
     * <p>
     * API subject to change in the future!
     *
     * @return
     */
    @RMPropertyIgnore
    public final String getPath() {
        return PathUtil.getPath(getPathSegments());
    }

}
