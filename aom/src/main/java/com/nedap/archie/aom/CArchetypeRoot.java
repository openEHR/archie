package com.nedap.archie.aom;

import com.nedap.archie.apath.PathSegment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_ARCHETYPE_ROOT")
public class CArchetypeRoot extends CComplexObject {
    @XmlElement(name="archetype_ref")
    private String archetypeRef;

    @Override
    public List<PathSegment> getPathSegments() {
        CAttribute parent = getParent();
        if(parent == null) {
            return new ArrayList<>();
        }
        List<PathSegment> segments = parent.getPathSegments();
        if(!segments.isEmpty()) {
            PathSegment parentSegment = segments.get(segments.size() - 1);
            parentSegment.setNodeId(getNodeId());
            parentSegment.setArchetypeRef(getArchetypeRef());
        }
        return segments;
    }

    public String getArchetypeRef() {
        return archetypeRef;
    }

    public void setArchetypeRef(String archetypeRef) {
        this.archetypeRef = archetypeRef;
    }
}
