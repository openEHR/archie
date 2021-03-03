package com.nedap.archie.serializer.adl.rules;

import com.nedap.archie.rules.Constraint;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;

/**
 * Created by pieter.bos on 15/06/16.
 */
public class ConstraintSerializer extends RuleElementSerializer<Constraint<?>> {

    public ConstraintSerializer(ADLRulesSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(Constraint<?> ruleElement) {
        builder.append("{");
        serializer.getDefinitionSerializer().appendCObject(ruleElement.getItem());
        builder.append("}");
    }
}
