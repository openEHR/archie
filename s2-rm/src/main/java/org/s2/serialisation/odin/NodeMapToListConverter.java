package org.s2.serialisation.odin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nedap.archie.odin.BaseMapToListConverter;
import org.s2.rm.base.patterns.data_structures.Node;

import java.util.List;
import java.util.Map;

/**
 * Jackson NEEDS a statically typed map to list converter, so
 */
public class NodeMapToListConverter extends BaseMapToListConverter<Node> {
    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(new TypeReference<Map<?, Node>>() {});
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(new TypeReference<List<Node>>() {});
    }
}
