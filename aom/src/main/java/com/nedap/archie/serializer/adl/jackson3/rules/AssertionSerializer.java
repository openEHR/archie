package com.nedap.archie.serializer.adl.jackson3.rules;

import com.nedap.archie.rules.Assertion;
import com.nedap.archie.serializer.adl.jackson3.ADLRulesSerializer;

/**
 * Created by pieter.bos on 15/06/16.
 */
public class AssertionSerializer extends RuleElementSerializer<Assertion> {

    public AssertionSerializer(ADLRulesSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(Assertion ruleElement) {
        if(ruleElement.getTag() != null) {
            builder.append(ruleElement.getTag());
            builder.append(": ");
        }

        serializer.serializeRuleElement(ruleElement.getExpression());
        builder.newline();

    }
}
