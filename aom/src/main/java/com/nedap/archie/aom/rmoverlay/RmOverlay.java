package com.nedap.archie.aom.rmoverlay;

import com.nedap.archie.aom.ArchetypeModelObject;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RmOverlay extends ArchetypeModelObject {

    @Nullable
    Map<String, RmAttributeVisibility> rmVisibility = new LinkedHashMap<>();

    public RmOverlay() {
    }

    public RmOverlay(Map<String, RmAttributeVisibility> rmVisibility) {
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
        RmOverlay rmOverlay = (RmOverlay) o;
        return Objects.equals(rmVisibility, rmOverlay.rmVisibility);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rmVisibility);
    }
}
