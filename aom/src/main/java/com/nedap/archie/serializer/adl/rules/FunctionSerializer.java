package com.nedap.archie.serializer.adl.rules;

import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.Function;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;

/**
 * Created by pieter.bos on 15/05/2017.
 */
public class FunctionSerializer extends RuleElementSerializer<Function>  {

    public FunctionSerializer(ADLRulesSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(Function ruleElement) {
        builder.append(ruleElement.getFunctionName());
        boolean first = true;

        builder.append("(");
        for(Expression expression:ruleElement.getArguments()) {
            if(!first) {
                builder.append(", ");
            }

            serializer.serializeRuleElement(expression);

            first = false;
        }

        builder.append(")");
    }
}
