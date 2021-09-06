package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.collect.Lists;
import com.nedap.archie.rules.Constant;
import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.evaluation.Evaluator;
import com.nedap.archie.rules.evaluation.RuleEvaluation;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.List;

/**
 * Created by pieter.bos on 01/04/16.
 */
public class ConstantEvaluator implements Evaluator<Constant<?>> {
    @Override
    public ValueList evaluate(RuleEvaluation<?> evaluation, Constant<?> statement) {
        return new ValueList(statement.getValue(), PrimitiveType.fromExpressionType(statement.getType()));
    }

    @Override
    public List<Class<?>> getSupportedClasses() {
        return Lists.newArrayList(Constant.class);
    }
}
