package com.nedap.archie.opt14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.ResourceAnnotations;
import com.nedap.archie.opt14.schema.*;

import java.util.LinkedHashMap;
import java.util.Map;

class AnnotationsConverter {
    public static void convertAnnotations(OPERATIONALTEMPLATE opt14, Archetype adl2Archetype) {
        if(opt14.getAnnotations() != null) {
            for(ANNOTATION annotation:opt14.getAnnotations()) {
                String path = annotation.getPath();
                if(adl2Archetype.getAnnotations() == null) {
                    adl2Archetype.setAnnotations(new ResourceAnnotations());
                    adl2Archetype.getAnnotations().setDocumentation(new LinkedHashMap<>());
                }
                Map<String, Map<String, String>> languageMap = adl2Archetype.getAnnotations().getDocumentation().get(opt14.getLanguage().getCodeString());
                if(languageMap == null) {
                    languageMap = new LinkedHashMap<>();
                    adl2Archetype.getAnnotations().getDocumentation().put(opt14.getLanguage().getCodeString(), languageMap);
                }
                Map<String, String> documentationMap = languageMap.get(path);
                if(documentationMap == null) {
                    documentationMap = new LinkedHashMap<>();
                    languageMap.put(path, documentationMap);
                }
                for(StringDictionaryItem item:annotation.getItems()) {
                    documentationMap.put(item.getId(), item.getValue());
                }
            }
        }
    }
}
