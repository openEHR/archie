package com.nedap.archie.template.betterjson;

import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.adl14.ADL14ConversionUtil;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.template.betterjson.parser.TemplateCTerminologyCode;
import com.nedap.archie.template.betterjson.parser.TemplateTermCode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ExternalTermBindingTranslationFixer {

    public void fixTranslations(ADL14ConversionConfiguration config, Archetype archetype) {
        fixInner(config, archetype);
        if(archetype instanceof Template) {
            Template template = (Template) archetype;
            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                fixInner(config, overlay);
            }
        }
    }

    private void fixInner(ADL14ConversionConfiguration config, Archetype archetype) {
        fixInner(config, archetype, archetype.getDefinition());
    }

    private void fixInner(ADL14ConversionConfiguration config, Archetype archetype, CObject cObject) {

        if(cObject instanceof TemplateCTerminologyCode) {
            TemplateCTerminologyCode templateCode = (TemplateCTerminologyCode) cObject;
            if(templateCode.getIncludedExternalTerminologyCodes() != null) {
                for (TemplateTermCode templateTermCode : templateCode.getIncludedExternalTerminologyCodes()) {
                    if(templateTermCode.getValue() == null || templateTermCode.getCode() == null || templateTermCode.getTerminologyId() == null) {
                        System.out.println("STRANGE TERM CODE FOUND");
                    }
                    try {
                        String value = templateTermCode.getValue();
                        Map<String, URI> uris = archetype.getTerminology().getTermBindings().get(templateTermCode.getTerminologyId());
                        URI uri = new ADL14ConversionUtil(config).convertToUri(TerminologyCode.createFromString(templateTermCode.getTerminologyId(), null, templateTermCode.getCode()));
                        String code = findTermbindingCode(uris, uri);
                        if(code != null) {
                            //add translation to terminology IF it was auto-generated
                            for(String language: archetype.getTerminology().getTermDefinitions().keySet()) {
                                Map<String, ArchetypeTerm> terms = archetype.getTerminology().getTermDefinitions().get(language);
                                ArchetypeTerm term = terms.get(code);
                                if(term != null && term.getText().contains("translation not known")) {
                                    term.setText(value);
                                    term.setDescription(value);
                                } else {
                                    term = new ArchetypeTerm();
                                    term.setCode(code);
                                    term.setText(value);
                                    term.setDescription(value);
                                    terms.put(code, term);
                                }
                            }
                        }
                    } catch (URISyntaxException e) {
                        //not a big problem, just a term will be missing from the terminology.
                        e.printStackTrace();
                    }

                }
            }
        }
        for(CAttribute attribute:cObject.getAttributes()) {
            fixInner(config, archetype, attribute);
        }

    }

    private String findTermbindingCode(Map<String, URI> uris, URI uri) {
        for(Map.Entry<String, URI> u:uris.entrySet()) {
            if(u.getValue().equals(uri)) {
                return u.getKey();
            }
        }
        return null;
    }

    private void fixInner(ADL14ConversionConfiguration config, Archetype archetype, CAttribute attribute) {

        for(CObject child:attribute.getChildren()) {
            fixInner(config, archetype, child);
        }
    }
}
