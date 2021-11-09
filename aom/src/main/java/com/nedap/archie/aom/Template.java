package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonAlias;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="TEMPLATE")
@XmlRootElement(name="archetype")
public class Template extends AuthoredArchetype {

    @Nullable
    private List<TemplateOverlay> templateOverlays = new ArrayList<>();

    @JsonAlias("overlays")
    public List<TemplateOverlay> getTemplateOverlays() {
        return templateOverlays;
    }

    public void setTemplateOverlays(List<TemplateOverlay> templateOverlays) {
        this.templateOverlays = templateOverlays;
    }

    public void addTemplateOverlay(TemplateOverlay overlay) {
        templateOverlays.add(overlay);
    }

    public TemplateOverlay getTemplateOverlay(String id) {
        for(TemplateOverlay overlay:templateOverlays) {
            if(overlay.getArchetypeId().getFullId().equals(id)) {
                return overlay;
            }
        }
        return null;
    }
}
