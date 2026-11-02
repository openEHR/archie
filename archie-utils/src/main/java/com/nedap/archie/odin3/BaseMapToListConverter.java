package com.nedap.archie.odin3;

import tools.jackson.databind.util.StdConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseMapToListConverter<T> extends StdConverter<Map<?, T>, List<T>> {

    @Override
    public List<T> convert(Map<?, T> map) {
        if (map == null) {
            return null;
        }
        return new ArrayList<>(map.values());
    }
}
