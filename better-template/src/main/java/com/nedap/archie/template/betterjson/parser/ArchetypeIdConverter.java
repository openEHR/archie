package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.rm.support.identification.TerminologyId;

public class ArchetypeIdConverter implements Converter<MarandArchetypeId, ArchetypeHRID> {
    @Override
    public ArchetypeHRID convert(MarandArchetypeId value) {
        return new ArchetypeHRID(value.getValue());
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(MarandArchetypeId.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(ArchetypeHRID.class);
    }
}
