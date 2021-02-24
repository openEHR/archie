package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.Template;
import com.nedap.archie.xml.types.XmlTemplate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TemplateAdapter extends XmlAdapter<XmlTemplate, Template> {
    @Override
    public Template unmarshal(XmlTemplate archetype) throws Exception {
        Template result = new Template();
        ArchetypeAdapter.fromXml(archetype, result);
        result.setTemplateOverlays(archetype.getTemplateOverlays());
        return result;
    }

    @Override
    public XmlTemplate marshal(Template v) throws Exception {
        return new XmlTemplate(v);
    }
}
