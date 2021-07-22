package com.nedap.archie.template.betterjson;

import com.nedap.archie.adl14.ADL14NodeIDConverter;
import com.nedap.archie.adl14.log.ConvertedCodeResult;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.terminology.ValueSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** converts value sets with old codes to new ones
 *  The ac-code is already ok
 **/
public class ValueSetFixer {



    public void convertValueSets(Archetype archetype) {
        convert(archetype);
        if(archetype instanceof Template) {
            Template template = (Template) archetype;
            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                convert(overlay);
            }
        }
    }

    private void convert(Archetype archetype) {
        Map<String, ConvertedCodeResult> convertedCodes = new LinkedHashMap<>();
        if(archetype.getTerminology() == null || archetype.getTerminology().getValueSets() == null) {
            return;
        }
        for(String key:archetype.getTerminology().getValueSets().keySet()) {
            ValueSet valueSet = archetype.getTerminology().getValueSets().get(key);
            List<String> codes = new ArrayList<>();
            for(String code:valueSet.getMembers()) {
                String newCode = ADL14NodeIDConverter.convertCode(code, "at");
                convertedCodes.put(code , new ConvertedCodeResult(code, newCode));
                codes.add(newCode);
            }
            valueSet.setMembers(codes);
        }
        ADL14NodeIDConverter.convertTermDefinitions(archetype, convertedCodes);
    }
}
