package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.nedap.archie.aom.TranslationDetails;
import com.nedap.archie.rm.support.identification.TerminologyId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TranslationConverter implements Converter<List<TranslationDetails>, Map<String, TranslationDetails>> {
    @Override
    public Map<String, TranslationDetails> convert(List<TranslationDetails> value) {
        LinkedHashMap<String, TranslationDetails> result = new LinkedHashMap();
        for(TranslationDetails v:value) {
            result.put(v.getLanguage().getCodeString(), v);
        }
        return result;

    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(new TypeReference<List<TranslationDetails>>() {});
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(new TypeReference<Map<String, TranslationDetails>>() {});
    }
}
