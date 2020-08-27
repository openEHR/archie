package com.nedap.archie.odin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.fasterxml.jackson.databind.util.Converter;
import com.nedap.archie.rm.datastructures.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Jackson NEEDS a statically types map to list converter, so
 */
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
