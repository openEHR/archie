package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.nedap.archie.rm.support.identification.TerminologyId;

public class TerminologyIdConverter implements Converter<TerminologyId, String> {
    @Override
    public String convert(TerminologyId value) {
        return value.getValue();
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(TerminologyId.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }
}
