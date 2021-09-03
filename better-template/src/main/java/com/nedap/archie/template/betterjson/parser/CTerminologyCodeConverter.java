package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.rm.support.identification.TerminologyId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CTerminologyCodeConverter {

    private final boolean replaceTermCodes;

    public CTerminologyCodeConverter(boolean replaceTermCodes) {
        this.replaceTermCodes = replaceTermCodes;
    }

    public void fixCTerminologyCodes(Archetype archetype) {
        fixInner(archetype);
        if(archetype instanceof Template) {
            Template template = (Template) archetype;
            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                fixInner(overlay);
            }
        }
    }

    private void fixInner(Archetype archetype) {
        fixInner(archetype, archetype.getDefinition());
    }

    private void fixInner(Archetype archetype, CObject cObject) {

        for(CAttribute attribute:cObject.getAttributes()) {
            fixInner(archetype, attribute);
        }
    }

    private void fixInner(Archetype archetype, CAttribute attribute) {
        Map<TemplateCTerminologyCode, CTerminologyCode> replacements = new LinkedHashMap<>();
        for(CObject cObject:attribute.getChildren()) {
            if(cObject instanceof TemplateCTerminologyCode) {
                TemplateCTerminologyCode templateCode = (TemplateCTerminologyCode) cObject;

                CTerminologyCode result = convert(templateCode);
                replacements.put(templateCode, result);

            }
            fixInner(archetype, cObject);
        }
        if(replaceTermCodes) {
            for (Map.Entry<TemplateCTerminologyCode, CTerminologyCode> replacement : replacements.entrySet()) {
                int index = attribute.getChildren().indexOf(replacement.getKey());
                attribute.getChildren().set(index, replacement.getValue());
            }
        }
    }

    public CTerminologyCode convert(TemplateCTerminologyCode value) {
        //TODO: this should be split into two separate converters/fixers
        if(!replaceTermCodes) {
            if (value.getTerminologyId() != null && value.getIncludedExternalTerminologyCodes() != null) {
                //convert external term codes to the non-parsed format. The converter will handle that
                //assuming only one terminology id for now - might not be correct!
                List<String> constraints = new ArrayList<>();
                boolean first = true;
                for (TemplateTermCode templateTermCode : value.getIncludedExternalTerminologyCodes()) {
                    if (first) {
                        constraints.add("[" + value.getTerminologyId().getValue() + "::" + templateTermCode.getCode() + "]");
                        first = false;
                    } else {
                        constraints.add(templateTermCode.getCode());
                    }
                }

                value.setConstraint(constraints);

                //TODO: check if it's possible that this is just a term binding to a terminology id?
            } else if (value.getTerminologyId() != null && !value.getTerminologyId().getValue().equalsIgnoreCase("local") && value.getConstraint() != null && !value.getConstraint().isEmpty()) {
                List<String> constraints = new ArrayList<>();
                constraints.addAll(value.getConstraint());
                String first = "[" + value.getTerminologyId() + "::" + constraints.get(0) + "]";
                constraints.set(0, first);

                value.setConstraint(constraints);


            }
        }
        CTerminologyCode result = new CTerminologyCode();
        result.setConstraint(value.getConstraint());
        result.setAssumedValue(value.getAssumedValue());
        result.setDefaultValue(value.getDefaultValue());
        result.setOccurrences(value.getOccurrences());
        return result;
    }

}
