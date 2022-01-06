package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.rules.RuleStatement;

import java.io.IOException;
import java.util.List;

public class RulesSectionDeserializer extends JsonDeserializer<RulesSection> {
    @Override
    public RulesSection deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if(p.isExpectedStartArrayToken()) {
            List<RuleStatement> list = ctxt.readValue(p, TypeFactory.defaultInstance().constructCollectionType(List.class, RuleStatement.class));
            if(list == null) {
                return null; //should not happen, just to be defensive.
            }
            RulesSection section = new RulesSection();
            section.setRules(list);
            return section;
        } else if(p.currentToken() == JsonToken.START_OBJECT) {
            CustomRulesSection parsed = ctxt.readValue(p, CustomRulesSection.class);
            if(parsed == null) {
                return null; //should not happen, just to be defensive.
            }
            RulesSection result = new RulesSection();
            result.setRules(parsed.getRules());
            result.setContent(parsed.getContent());
            return result;
        } else if(p.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        throw new InvalidFormatException(p, "Expected an array of Rules Statements, a Rules section or null - got something else instead", null, RulesSection.class);
    }
}

class CustomRulesSection {
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
