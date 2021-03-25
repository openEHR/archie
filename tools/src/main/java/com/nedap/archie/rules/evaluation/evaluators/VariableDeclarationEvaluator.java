package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.collect.Lists;
import com.nedap.archie.rules.BuiltinVariable;
import com.nedap.archie.rules.ExpressionVariable;
import com.nedap.archie.rules.QueryVariable;
import com.nedap.archie.rules.VariableDeclaration;
import com.nedap.archie.rules.evaluation.Evaluator;
import com.nedap.archie.rules.evaluation.RuleEvaluation;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.List;

/**
 * Created by pieter.bos on 31/03/16.
 */
public class VariableDeclarationEvaluator implements Evaluator<VariableDeclaration> {

    @Override
    public ValueList evaluate(RuleEvaluation<?> evaluation, VariableDeclaration declaration) {
        String variableName = declaration.getName();
        ValueList valueList = calculateValue(evaluation, declaration);
        evaluation.getVariableMap().put(variableName, valueList);
        return valueList;

    }

    private ValueList calculateValue(RuleEvaluation<?> evaluation, VariableDeclaration declaration) {
        if(declaration instanceof ExpressionVariable) {
            return evaluation.evaluate(((ExpressionVariable) declaration).getExpression());
        } else if (declaration instanceof QueryVariable) {
            //TODO:
            return null;
        } else if (declaration instanceof BuiltinVariable) {
            //TODO
            return null;
        }
        return null;
    }

    @Override
    public List<Class<?>> getSupportedClasses() {
        return Lists.newArrayList(
                VariableDeclaration.class,
                BuiltinVariable.class,
                QueryVariable.class,
                ExpressionVariable.class);
    }
}
