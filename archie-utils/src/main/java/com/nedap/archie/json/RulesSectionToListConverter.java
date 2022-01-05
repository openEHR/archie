package com.nedap.archie.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.rules.RuleStatement;

import java.util.ArrayList;
import java.util.List;

public class RulesSectionToListConverter implements Converter<RulesSection, List<RuleStatement>> {
    @Override
    public List<RuleStatement> convert(RulesSection value) {
        if(value == null) {
            return null;
        }
        return value.getRules();
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(RulesSection.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionLikeType(List.class, RuleStatement.class);
    }
}
