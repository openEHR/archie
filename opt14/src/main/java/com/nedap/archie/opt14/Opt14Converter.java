package com.nedap.archie.opt14;

import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.Template;

public class Opt14Converter {
    
    public Template convert(OPERATIONALTEMPLATE opt14) {
        Template template = new Template();
        template.setArchetypeId(new ArchetypeHRID("openEHR-EHR-" + opt14.getDefinition().getRmTypeName() + "." + opt14.getTemplateId().getValue() + "v1.0.0"));
        template.setParentArchetypeId(opt14.getDefinition().getArchetypeId().getValue());
        DescriptionConverter.convert(template, opt14);

        new DefinitionConverter().convert(template, opt14);
        return template;
    }
}
