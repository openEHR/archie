package com.nedap.archie.rules.evaluation.evaluators;

import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.OperatorKind;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 07/02/2017.
 */
public class BinaryStringOperandEvaluator {


    private BinaryOperatorEvaluator mainEvaluator;

    public BinaryStringOperandEvaluator(BinaryOperatorEvaluator mainEvaluator) {
        this.mainEvaluator = mainEvaluator;
    }

    protected Value<Boolean> evaluateMultipleValuesStringRelOp(BinaryOperator statement, ValueList leftValues, ValueList rightValues) {

        for(Value<?> leftValue:leftValues.getValues()) {
            for (Value<?> rightValue:rightValues.getValues()) {
                Value<Boolean> evaluatedRelOp = evaluateBooleanRelOp(statement, leftValue.getValue(), rightValue.getValue(), mainEvaluator.getPaths(leftValue, rightValue));
                if (((Boolean) evaluatedRelOp.getValue()).booleanValue()) {
                    return evaluatedRelOp;
                }
            }
        }
        return new Value<>(false, mainEvaluator.getAllPaths(leftValues, rightValues));
    }


    private Value<Boolean> evaluateBooleanRelOp(BinaryOperator statement, Object leftValue, Object rightValue, List<String> paths) {
        if(leftValue == null || rightValue == null) {
            return new Value<>(mainEvaluator.evaluateNullRelOp(statement.getOperator(), leftValue, rightValue), paths);
        }
        else if(leftValue instanceof String && rightValue instanceof String) {
            return new Value<>(evaluateBooleanRelOp(statement.getOperator(),
                    (String) leftValue,
                    (String) rightValue
            ), paths);
        } else {
            throw new IllegalStateException("operand types not both String: " + leftValue.getClass().getSimpleName() + " and " + rightValue.getClass().getSimpleName());
        }
    }

    private Boolean evaluateBooleanRelOp(OperatorKind operator, String leftValue, String rightValue) {
        switch(operator) {
            case eq:
                return Objects.equals(leftValue, rightValue);
            case ne:
                return !Objects.equals(leftValue, rightValue);
            default:
                throw new IllegalArgumentException("Not a boolean operator with boolean operands: " + operator);
        }
    }
}
