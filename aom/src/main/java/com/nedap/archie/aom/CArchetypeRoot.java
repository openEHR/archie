package com.nedap.archie.aom;

import com.nedap.archie.paths.PathSegment;

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
            segments.set(segments.size() - 1, segments.get(segments.size() - 1).withNodeId(getNodeId()).withArchetypeRef(getArchetypeRef()));
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
