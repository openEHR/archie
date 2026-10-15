package com.nedap.archie.json3;

import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.rules.RuleStatement;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.List;

public class RulesSectionDeserializer extends ValueDeserializer<RulesSection> {

    @Override
    public RulesSection deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        if (p.isExpectedStartArrayToken()) {
            List<RuleStatement> list = ctxt.readValue(p, ctxt.getTypeFactory().constructCollectionType(List.class, RuleStatement.class));
            if (list == null) {
                return null;
            }
            RulesSection section = new RulesSection();
            section.setRules(list);
            return section;
        } else if (p.currentToken() == JsonToken.START_OBJECT || p.currentToken() == JsonToken.PROPERTY_NAME) {
            CustomRulesSectionJ3 parsed = ctxt.readValue(p, CustomRulesSectionJ3.class);
            if (parsed == null) {
                return null;
            }
            RulesSection result = new RulesSection();
            result.setRules(parsed.getRules());
            result.setContent(parsed.getContent());
            return result;
        } else if (p.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        throw new InvalidFormatException(p, "Expected an array of Rules Statements, a Rules section or null - got something else instead", null, RulesSection.class);
    }
}

class CustomRulesSectionJ3 {
    String content;
    List<RuleStatement> rules;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<RuleStatement> getRules() {
        return rules;
    }

    public void setRules(List<RuleStatement> rules) {
        this.rules = rules;
    }
}
