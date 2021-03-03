package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.collect.Lists;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.ForAllStatement;
import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.evaluation.Evaluator;
import com.nedap.archie.rules.evaluation.RuleEvaluation;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 10/05/16.
 */
public class ForAllEvaluator implements Evaluator<ForAllStatement> {
    @Override
    public ValueList evaluate(RuleEvaluation<?> evaluation, ForAllStatement statement) {
        Expression pathExpression = statement.getPathExpression();
        String variableName = statement.getVariableName();
        Expression toEvaluate = statement.getAssertion();

        ValueList pathValues = evaluation.evaluate(pathExpression);

        List<String> allPaths = new ArrayList<String>();

        boolean resultingCheck = true;
        for(Value value:pathValues.getValues()) {
            if(value.getPaths().size() > 1) {
                throw new IllegalStateException("for all path can only have one path value per value");
            }
            Object context = value.getValue();
            String path = (String) value.getPaths().get(0);

            // according to the latest openEHR docs, this should be 'objectreference'.
            // We could change the name of the java class

            RMObjectWithPath rmObjectWithPath = new RMObjectWithPath(context, path);
            ValueList valueList = new ValueList(rmObjectWithPath);
            valueList.setType(PrimitiveType.ObjectReference);

            //set the variable
            evaluation.getVariableMap().put(variableName, valueList);
            //evaluate
            ValueList evaluated = evaluation.evaluate(toEvaluate);
            allPaths.addAll(evaluated.getAllPaths());
            if(evaluated.getType() == PrimitiveType.Boolean) {
                for (Value evaluatedValue : evaluated.getValues()) {
                    if (evaluatedValue.getValue() != null && !((Boolean)evaluatedValue.getValue()).booleanValue()) {
                        resultingCheck = false;
                    }
                }
            } else if(!evaluated.getSingleBooleanResult()){
                resultingCheck = false;
            } else if(evaluated.getType() == PrimitiveType.Integer || evaluated.getType() == PrimitiveType.Real) {
                throw new UnsupportedOperationException("cannot convert type to boolean yet: " + evaluated.getType());
            }
        }
        evaluation.getVariableMap().put(variableName, null);
        return new ValueList(Lists.newArrayList(new Value(resultingCheck, allPaths)));
    }

    @Override
    public List<Class<?>> getSupportedClasses() {
        return Lists.newArrayList(ForAllStatement.class);
    }
}
