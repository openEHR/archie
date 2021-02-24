package com.nedap.archie.xml.types;

import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class XmlTemplate extends XmlArchetype {

    @XmlElement(name="template_overlays")
    private List<TemplateOverlay> templateOverlays;

    public XmlTemplate() {
        super();
    }

    public XmlTemplate(Template template) {
        super(template);
        this.templateOverlays = template.getTemplateOverlays();
    }

    public List<TemplateOverlay> getTemplateOverlays() {
        return templateOverlays;
    }

    public void setTemplateOverlays(List<TemplateOverlay> templateOverlays) {
        this.templateOverlays = templateOverlays;
    }
}
