package com.nedap.archie.serializer.adl.rules;

import com.nedap.archie.rules.VariableReference;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;

/**
 * Created by pieter.bos on 15/06/16.
 */
public class VariableReferenceSerializer extends RuleElementSerializer<VariableReference> {

    public VariableReferenceSerializer(ADLRulesSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(VariableReference ruleElement) {
        StringBuilder toAppend = new StringBuilder();
        toAppend.append("$");
        toAppend.append(ruleElement.getDeclaration().getName());

        if(builder.getCurrentLineLength() + toAppend.length() > ADLRulesSerializer.NEW_LINE_LIMIT) {
            builder.newline();
        }
        builder.append(toAppend);
    }
}
