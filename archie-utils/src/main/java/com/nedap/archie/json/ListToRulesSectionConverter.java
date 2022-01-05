package com.nedap.archie.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.rules.RuleStatement;

import java.util.List;

public class ListToRulesSectionConverter implements Converter<List<RuleStatement>, RulesSection> {
    @Override
    public RulesSection convert(List<RuleStatement> value) {
        if(value == null) {
            return null;
        }
        RulesSection converted = new RulesSection();
        converted.setRules(value);
        return converted;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionLikeType(List.class, RuleStatement.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(RulesSection.class);
    }
}
