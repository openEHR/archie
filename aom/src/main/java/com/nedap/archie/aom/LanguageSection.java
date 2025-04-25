package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nedap.archie.base.terminology.TerminologyCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Model of the language section in ADL. In the AOM spec these are fields on the AuthoredResource, but this class is not
 * in the AOM spec and you should not need to use it directly. Use the methods on AuthoredResource instead.
 * It is included for proper ODIN parsing.
 * See AuthoredResource for more information about this design choice.
 *
 * Created by pieter.bos on 02/11/15.
 */
public class LanguageSection extends ArchetypeModelObject {

    private TerminologyCode originalLanguage;
    private Map<String, TranslationDetails> translations = new ConcurrentHashMap<>();


    @JsonProperty
    public TerminologyCode getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(TerminologyCode originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    @JsonProperty
    public Map<String, TranslationDetails> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, TranslationDetails> translations) {
        this.translations = translations;
    }

}
