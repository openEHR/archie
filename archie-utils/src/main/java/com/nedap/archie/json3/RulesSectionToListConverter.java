package com.nedap.archie.json3;

import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.rules.RuleStatement;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.StdConverter;

import java.util.List;

public class RulesSectionToListConverter extends StdConverter<RulesSection, List<RuleStatement>> {

    @Override
    public List<RuleStatement> convert(RulesSection value) {
        if (value == null) {
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
