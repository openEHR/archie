package com.nedap.archie.openehr.serialisation.odin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nedap.archie.odin.BaseMapToListConverter;
import org.openehr.rm.datavalues.TermMapping;

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

