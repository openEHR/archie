package com.nedap.archie.odin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.nedap.archie.rm.datavalues.TermMapping;

import java.util.List;
import java.util.Map;

public class TermMappingMapToListConverter extends BaseMapToListConverter<TermMapping> {

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(new TypeReference<Map<?, TermMapping>>() {});
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(new TypeReference<List<TermMapping>>() {});
    }
}

