package com.nedap.archie.serializer.adl.rules;

import com.nedap.archie.rules.Constant;
import com.nedap.archie.rules.ExpressionType;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;
import com.nedap.archie.rules.ReferenceType;

/**
 * Created by pieter.bos on 15/06/16.
 */
public class ConstantSerializer extends RuleElementSerializer<Constant<?>> {
    public ConstantSerializer(ADLRulesSerializer adlRulesSerializer) {
        super(adlRulesSerializer);
    }

    @Override
    public void serialize(Constant<?> ruleElement) {
        if(ruleElement.getType() == ExpressionType.STRING) {
            builder.append("\"");
            builder.append(ruleElement.getValue());
            builder.append("\"");
        } else {
            builder.append(ruleElement.getValue());
        }
    }
}
