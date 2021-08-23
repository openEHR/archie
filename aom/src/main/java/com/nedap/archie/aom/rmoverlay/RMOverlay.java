package com.nedap.archie.aom.rmoverlay;

import com.nedap.archie.aom.ArchetypeModelObject;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@XmlType(name = "RM_ATTRIBUTE_VISIBILITY")
public class RMOverlay extends ArchetypeModelObject {

    @Nullable
    @XmlElement(name="rm_visibility")
    Map<String,RmAttributeVisibility> rmVisibility = new LinkedHashMap<>();

    public RMOverlay() {
    }

    public RMOverlay(Map<String, RmAttributeVisibility> rmVisibility) {
        this.rmVisibility = rmVisibility;
    }

    public Map<String, RmAttributeVisibility> getRmVisibility() {
        return rmVisibility;
    }

    public void setRmVisibility(Map<String, RmAttributeVisibility> rmVisibility) {
        this.rmVisibility = rmVisibility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RMOverlay rmOverlay = (RMOverlay) o;
        return Objects.equals(rmVisibility, rmOverlay.rmVisibility);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rmVisibility);
    }
}
