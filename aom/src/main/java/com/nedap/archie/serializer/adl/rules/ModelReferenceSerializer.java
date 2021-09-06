package com.nedap.archie.serializer.adl.rules;

import com.nedap.archie.rules.ModelReference;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;

/**
 * Created by pieter.bos on 15/06/16.
 */
public class ModelReferenceSerializer extends RuleElementSerializer<ModelReference> {


    public ModelReferenceSerializer(ADLRulesSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(ModelReference reference) {
        StringBuilder toAppend = new StringBuilder();

        if(reference.getVariableReferencePrefix() != null) {
            toAppend.append("$");
            toAppend.append(reference.getVariableReferencePrefix());
        }
        toAppend.append(reference.getPath());

        if(builder.getCurrentLineLength() + toAppend.length() > ADLRulesSerializer.NEW_LINE_LIMIT) {
            builder.newline();
        }
        builder.append(toAppend);
    }

}
