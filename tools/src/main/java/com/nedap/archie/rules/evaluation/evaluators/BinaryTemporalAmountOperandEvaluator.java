package com.nedap.archie.rules.evaluation.evaluators;

import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.OperatorKind;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;
import org.threeten.extra.PeriodDuration;

import java.time.*;
import java.time.temporal.TemporalAmount;
import java.util.List;

public class BinaryTemporalAmountOperandEvaluator {
    private BinaryOperatorEvaluator mainEvaluator;
    public BinaryTemporalAmountOperandEvaluator(BinaryOperatorEvaluator mainEvaluator) {
        this.mainEvaluator = mainEvaluator;
    }

    protected Value<Boolean> evaluateMultipleValuesDateRelOp(BinaryOperator statement, ValueList leftValues, ValueList rightValues) {
        for (Value<?> leftValue : leftValues.getValues()) {
            for (Value<?> rightValue : rightValues.getValues()) {
                Value<Boolean> evaluatedRelOp = evaluateBooleanRelOp(statement, leftValue.getValue(), rightValue.getValue(), mainEvaluator.getPaths(leftValue, rightValue));
                if (evaluatedRelOp.getValue().booleanValue()) {
                    return evaluatedRelOp;
                }
            }
        }

        return new Value<>(false, mainEvaluator.getAllPaths(leftValues, rightValues));
    }

    private Value<Boolean> evaluateBooleanRelOp(BinaryOperator statement, Object leftValue, Object rightValue, List<String> paths) {
        if(leftValue == null || rightValue == null) {
            return new Value<>(mainEvaluator.evaluateNullRelOp(statement.getOperator(), leftValue, rightValue), paths);
        } else if (leftValue instanceof TemporalAmount && rightValue instanceof TemporalAmount) {
            return new Value<>(evaluateBooleanRelOp(statement.getOperator(), (TemporalAmount) leftValue, (TemporalAmount) rightValue), paths);
        } else {
            throw new IllegalStateException("operand types not supported: " + leftValue.getClass().getSimpleName() + " and " + rightValue.getClass().getSimpleName());
        }
    }

    /**
     * Evaluate values that are an instance of TemporalAmount (Currently supported: Period and Duration)
     *
     * @param operator the operator of the expression
     * @param left left value
     * @param right right value
     *
     * @return A {@link Boolean} result of the comparison
     */
    private Boolean evaluateBooleanRelOp(OperatorKind operator, TemporalAmount left, TemporalAmount right) {
        if (!left.getClass().equals(right.getClass())) {
            throw new IllegalArgumentException("non matching classes not supported: " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName() + ".");
        }

        switch (operator) {
            case ge:
                if (isEqual(left, right)) return true;
            case gt:
                return isGreater(left, right);
            case le:
                if (isEqual(left, right)) return true;
            case lt:
                return isLess(left, right);
            case eq:
                return isEqual(left, right);
            case ne:
                return !isEqual(left, right);
            default:
                throw new IllegalArgumentException("Not a boolean operator with boolean operands: " + operator);
        }
    }

    private Boolean isLess(TemporalAmount left, TemporalAmount right) {
        if (left instanceof Period && right instanceof Period) {
            return periodToDaysEstimate((Period) left) - periodToDaysEstimate((Period) right) < 0;
        } else if (left instanceof Duration && right instanceof Duration) {
            return ((Duration) left).minus((Duration) right).isNegative();
        } else if (left instanceof PeriodDuration && right instanceof PeriodDuration) {
            return periodDurationToSecondsEstimate((PeriodDuration) left) - periodDurationToSecondsEstimate((PeriodDuration) right) < 0;
        } else {
            throw new IllegalArgumentException("TemporalAmount class not supported: " + left.getClass().getSimpleName());
        }
    }

    private Boolean isGreater(TemporalAmount left, TemporalAmount right) {
        if (left instanceof Period && right instanceof Period) {
            return periodToDaysEstimate((Period) left) - periodToDaysEstimate((Period) right) > 0;
        } else if (left instanceof Duration && right instanceof Duration) {
            return ((Duration) right).minus((Duration) left).isNegative();
        } else if (left instanceof PeriodDuration && right instanceof PeriodDuration) {
            return periodDurationToSecondsEstimate((PeriodDuration) left) - periodDurationToSecondsEstimate((PeriodDuration) right) > 0;
        } else {
            throw new IllegalArgumentException("TemporalAmount class not supported: " + left.getClass().getSimpleName());
        }
    }

    private Boolean isEqual(TemporalAmount left, TemporalAmount right) {
        if (left instanceof Period && right instanceof Period) {
            return periodToDaysEstimate((Period) left) - periodToDaysEstimate((Period) right) == 0;
        } else if (left instanceof Duration && right instanceof Duration) {
            return right.equals(left);
        } else if (left instanceof PeriodDuration && right instanceof PeriodDuration) {
            return periodDurationToSecondsEstimate((PeriodDuration) left) - periodDurationToSecondsEstimate((PeriodDuration) right) == 0;
        } else {
            throw new IllegalArgumentException("TemporalAmount class not supported: " + left.getClass().getSimpleName());
        }
    }

    /**
     * Period is not really comparable as it depends on what date it's used with.
     * Example: 30 days is not always 1 month.
     * <p>
     * This method calculates the estimated amount of days in a Period, so it can be used to compare.
     *
     * @param period The period of which the estimated days should be returned.
     * @return An estimated amount of days for the given Period.
     */
    private int periodToDaysEstimate(Period period) {
        if (period == null) return 0;
        return (period.getYears() * 12 + period.getMonths()) * 30 + period.getDays();
    }

    private int periodDurationToSecondsEstimate(PeriodDuration periodDuration) {
        int seconds = 0;

        if (periodDuration.getPeriod() != null && !periodDuration.getPeriod().isZero()) {
            seconds += periodToDaysEstimate(periodDuration.getPeriod()) * (24 * 60 * 60);
        }

        if (periodDuration.getDuration() != null && !periodDuration.getDuration().isZero()) {
            seconds += periodDuration.getDuration().getSeconds();
        }

        return seconds;
    }
}
