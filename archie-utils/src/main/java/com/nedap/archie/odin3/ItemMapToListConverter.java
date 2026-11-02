package com.nedap.archie.odin3;

import com.nedap.archie.rm.datastructures.Item;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import java.util.List;
import java.util.Map;

public class ItemMapToListConverter extends BaseMapToListConverter<Item> {

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(new TypeReference<Map<?, Item>>() {});
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(new TypeReference<List<Item>>() {});
    }
}
