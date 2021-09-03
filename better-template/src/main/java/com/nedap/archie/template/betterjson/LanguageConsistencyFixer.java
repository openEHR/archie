package com.nedap.archie.template.betterjson;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.archetypevalidator.ErrorType;

import java.util.List;

public class LanguageConsistencyFixer {

    public void fixLanguageConsistency(Archetype archetype) {

        fixInner(archetype);
        if(archetype instanceof Template) {
            Template template = (Template) archetype;
            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                fixLanguageConsistency(overlay);
            }
        }

    }

    private void fixInner(Archetype archetype) {

        List<String> codes = archetype.getTerminology().allCodes();
        for(String code:codes) {
            for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
                if(!archetype.getTerminology().getTermDefinitions().get(language).containsKey(code)) {

                    ArchetypeTerm term = new ArchetypeTerm();
                    term.setCode(code);
                    term.setText("translation missing");
                    term.setDescription("translation missing");
                    archetype.getTerminology().getTermDefinitions().get(language).put(code, term);
                }
            }
        }
    }

}
