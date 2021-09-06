package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.collect.Lists;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.evaluation.Evaluator;
import com.nedap.archie.rules.evaluation.RuleEvaluation;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.List;

/**
 * Created by pieter.bos on 31/03/16.
 */
public class AssertionEvaluator implements Evaluator<Assertion> {
    @Override
    public ValueList evaluate(RuleEvaluation<?> evaluation, Assertion statement) {
        if(statement.getExpression() != null) {
            ValueList valueList = evaluation.evaluate(statement.getExpression());
            if (valueList.getType() == PrimitiveType.Boolean) {
                evaluation.assertionEvaluated(statement.getTag(), statement.getExpression(), valueList);
                return valueList;
            } else {
                //variable declaration?
                //throw new RuntimeException("assertion with type " + value.getType() + " found - should be boolean!");
            }
        }
        return null;
        //TODO: this assertion can be several things:
        //1. A boolean assertion. False means show an error
        //2. A 'set field based on value'

    }

    @Override
    public List<Class<?>> getSupportedClasses() {
        return Lists.newArrayList(Assertion.class);
    }
}
