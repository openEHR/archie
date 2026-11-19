package com.nedap.archie.aom;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="TEMPLATE")
@XmlRootElement(name="template")
public class Template extends AuthoredArchetype {

    @XmlElement(name="template_overlay")
    private List<TemplateOverlay> templateOverlays = new ArrayList<>();

    public List<TemplateOverlay> getTemplateOverlays() {
        return templateOverlays;
    }

    public void setTemplateOverlays(List<TemplateOverlay> templateOverlays) {
        this.templateOverlays = templateOverlays;
        setOwningTemplateOnOverlays();
    }

    public void addTemplateOverlay(TemplateOverlay overlay) {
        templateOverlays.add(overlay);
        overlay.setOwningTemplate(this);
    }

    // Invoked by Jaxb Marshaller after unmarshalling. JAXB sets the templateOverlays field directly, bypassing the setter
    @Override
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        super.afterUnmarshal(unmarshaller, parent);
        setOwningTemplateOnOverlays();
    }

    private void setOwningTemplateOnOverlays() {
        if (templateOverlays != null) {
            for (TemplateOverlay overlay : templateOverlays) {
                overlay.setOwningTemplate(this);
            }
        }
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
