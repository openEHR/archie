package com.nedap.archie.rules.evaluation.evaluators;

import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.OperatorKind;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.List;

public class BinaryTemporalOperandEvaluator {
    private BinaryOperatorEvaluator mainEvaluator;
    public BinaryTemporalOperandEvaluator(BinaryOperatorEvaluator mainEvaluator) {
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
        } else if (leftValue instanceof Temporal && rightValue instanceof Temporal) {
            return new Value<>(evaluateBooleanRelOp(statement.getOperator(), (Temporal) leftValue, (Temporal) rightValue), paths);
        } else {
            throw new IllegalStateException("operand types not supported: " + leftValue.getClass().getSimpleName() + " and " + rightValue.getClass().getSimpleName());
        }
    }

    /**
     * Evaluate values that are an instance of Temporal (LocalDate, LocalTime, LocalDateTime, OffsetDateTime and ZonedDateTime)
     *
     * @param operator the operator of the expression
     * @param left left value
     * @param right right value
     *
     * @return A {@link Boolean} result of the comparison
     */
    private Boolean evaluateBooleanRelOp(OperatorKind operator, Temporal left, Temporal right) {
        if (!left.getClass().equals(right.getClass())) {
            throw new IllegalArgumentException("non matching classes not supported: " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName() + ".");
        }

        switch (operator) {
            case ge:
                if (isEqual(left, right)) return true;
            case gt:
                return isAfter(left, right);
            case le:
                if (isEqual(left, right)) return true;
            case lt:
                return isBefore(left, right);
            case eq:
                return isEqual(left, right);
            case ne:
                return !isEqual(left, right);
            default:
                throw new IllegalArgumentException("Not a boolean operator with boolean operands: " + operator);
        }
    }

    private Boolean isBefore(Temporal left, Temporal right) {
        if (left instanceof LocalDate && right instanceof LocalDate) {
            return ((LocalDate) left).isBefore((LocalDate) right);
        } else if (left instanceof LocalTime && right instanceof LocalTime) {
            return ((LocalTime) left).isBefore((LocalTime) right);
        } else if (left instanceof LocalDateTime && right instanceof LocalDateTime) {
            return ((LocalDateTime) left).isBefore((LocalDateTime) right);
        } else if (left instanceof OffsetDateTime && right instanceof OffsetDateTime) {
            return ((OffsetDateTime) left).isBefore((OffsetDateTime) right);
        } else if (left instanceof ZonedDateTime && right instanceof ZonedDateTime) {
            return ((ZonedDateTime) left).isBefore((ZonedDateTime) right);
        } else {
            throw new IllegalArgumentException("Temporal class not supported: " + left.getClass().getSimpleName());
        }
    }

    private Boolean isAfter(Temporal left, Temporal right) {
        if (left instanceof LocalDate && right instanceof LocalDate) {
            return ((LocalDate) left).isAfter((LocalDate) right);
        } else if (left instanceof LocalTime && right instanceof LocalTime) {
            return ((LocalTime) left).isAfter((LocalTime) right);
        } else if (left instanceof LocalDateTime && right instanceof LocalDateTime) {
            return ((LocalDateTime) left).isAfter((LocalDateTime) right);
        } else if (left instanceof OffsetDateTime && right instanceof OffsetDateTime) {
            return ((OffsetDateTime) left).isAfter((OffsetDateTime) right);
        } else if (left instanceof ZonedDateTime && right instanceof ZonedDateTime) {
            return ((ZonedDateTime) left).isAfter((ZonedDateTime) right);
        } else {
            throw new IllegalArgumentException("Temporal class not supported: " + left.getClass().getSimpleName());
        }
    }

    private Boolean isEqual(Temporal left, Temporal right) {
        if (left instanceof LocalDate && right instanceof LocalDate) {
            return ((LocalDate) left).isEqual((LocalDate) right);
        } else if (left instanceof LocalTime && right instanceof LocalTime) {
            return !isBefore(left, right) && !isAfter(left, right);
        } else if (left instanceof LocalDateTime && right instanceof LocalDateTime) {
            return ((LocalDateTime) left).isEqual((LocalDateTime) right);
        } else if (left instanceof OffsetDateTime && right instanceof OffsetDateTime) {
            return ((OffsetDateTime) left).isEqual((OffsetDateTime) right);
        } else if (left instanceof ZonedDateTime && right instanceof ZonedDateTime) {
            return ((ZonedDateTime) left).isEqual((ZonedDateTime) right);
        } else {
            throw new IllegalArgumentException("Temporal class not supported: " + left.getClass().getSimpleName());
        }
    }
}
