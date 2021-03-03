package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.collect.Lists;
import com.nedap.archie.rules.VariableReference;
import com.nedap.archie.rules.evaluation.Evaluator;
import com.nedap.archie.rules.evaluation.RuleEvaluation;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.List;

/**
 * Created by pieter.bos on 04/04/16.
 */
public class VariableReferenceEvaluator implements Evaluator<VariableReference> {
    @Override
    public ValueList evaluate(RuleEvaluation<?> evaluation, VariableReference statement) {
        return evaluation.getVariableMap().get(statement.getDeclaration().getName());
    }

    @Override
    public List<Class<?>> getSupportedClasses() {
        return Lists.newArrayList(VariableReference.class);
    }
}
