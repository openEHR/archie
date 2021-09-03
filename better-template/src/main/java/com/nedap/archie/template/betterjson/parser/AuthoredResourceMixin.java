package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nedap.archie.aom.TranslationDetails;

import java.util.Map;

public interface AuthoredResourceMixin {

    @JsonDeserialize(converter = TranslationConverter.class)
    void setTranslations(Map<String, TranslationDetails> translations);
}
