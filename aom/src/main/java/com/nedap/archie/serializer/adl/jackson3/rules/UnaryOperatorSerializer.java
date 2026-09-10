package com.nedap.archie.serializer.adl.jackson3.rules;

import com.nedap.archie.rules.UnaryOperator;
import com.nedap.archie.serializer.adl.jackson3.ADLRulesSerializer;

/**
 * Created by pieter.bos on 15/06/16.
 */
public class UnaryOperatorSerializer extends RuleElementSerializer<UnaryOperator> {

    public UnaryOperatorSerializer(ADLRulesSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(UnaryOperator ruleElement) {
        builder.append(ruleElement.getOperator().getDefaultCode());
        builder.append(" ");
        serializer.serializeRuleElement(ruleElement.getOperand());
    }
}
