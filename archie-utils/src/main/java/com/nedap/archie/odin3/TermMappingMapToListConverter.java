package com.nedap.archie.odin3;

import com.nedap.archie.rm.datavalues.TermMapping;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

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
