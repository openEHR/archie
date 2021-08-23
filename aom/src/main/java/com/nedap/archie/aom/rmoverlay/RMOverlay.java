package com.nedap.archie.aom.rmoverlay;

import com.nedap.archie.aom.ArchetypeModelObject;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RMOverlay extends ArchetypeModelObject {

    @Nullable
    Map<String, RMAttributeVisibility> rmVisibility = new LinkedHashMap<>();

    public RMOverlay() {
    }

    public RMOverlay(Map<String, RMAttributeVisibility> rmVisibility) {
        this.rmVisibility = rmVisibility;
    }

    public Map<String, RMAttributeVisibility> getRmVisibility() {
        return rmVisibility;
    }

    public void setRmVisibility(Map<String, RMAttributeVisibility> rmVisibility) {
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
