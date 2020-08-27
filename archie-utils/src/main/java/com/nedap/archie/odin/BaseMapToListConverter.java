package com.nedap.archie.odin;

import com.fasterxml.jackson.databind.util.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseMapToListConverter <T> implements Converter<Map<?, T>, List<T>> {

    @Override
    public List<T> convert(Map<?, T> map) {
        if(map == null) {
            return null;
        }
        return new ArrayList<>(map.values());
    }
}
