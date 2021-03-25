package com.nedap.archie.serializer.adl.rules;


import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;
import com.nedap.archie.serializer.adl.ADLStringBuilder;


/**
 * Created by pieter.bos on 15/06/16.
 */
public class BinaryOperatorSerializer extends RuleElementSerializer<BinaryOperator> {
    public BinaryOperatorSerializer(ADLRulesSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(BinaryOperator operator) {
        switch (operator.getOperator()) {
            case implies:
                serializer.serializeRuleElement(operator.getLeftOperand());
                builder.append(" ");
                builder.append(operator.getOperator().getDefaultCode());
                builder.newIndentedLine();
                serializer.serializeRuleElement(operator.getRightOperand());
                builder.unindent();
                break;
            case eq:
            case gt:
            case lt:
            case ne:
            case le:
            case ge:
                serializer.serializeRuleElement(operator.getLeftOperand());
                builder.append(" ");
                builder.append(operator.getOperator().getDefaultCode());
                builder.append(" ");
                serializer.getBuilder().indent();
                serializer.serializeRuleElement(operator.getRightOperand());
                builder.unindent();
                break;
            default:
                serializer.serializeRuleElement(operator.getLeftOperand());
                builder.append(" ");
                builder.append(operator.getOperator().getDefaultCode());
                builder.append(" ");
                serializer.serializeRuleElement(operator.getRightOperand());
        }

    }
}
