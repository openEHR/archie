package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.nedap.archie.rules.evaluation.DummyRulesPrimitiveObjectParent;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeConstraint;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.query.AOMPathQuery;
import com.nedap.archie.apath.APathQuery;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import com.nedap.archie.rules.OperatorKind;
import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.evaluation.Evaluator;
import com.nedap.archie.rules.evaluation.RuleEvaluation;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.nedap.archie.rules.evaluation.evaluators.FunctionUtil.checkAndHandleNull;


/**
 * Created by pieter.bos on 01/04/16.
 */
public class BinaryOperatorEvaluator implements Evaluator<BinaryOperator> {
    private static final double EPSILON = 0.00001d;
    private static final EnumSet<PrimitiveType> SUPPORTED_TEMPORAL_TYPES = EnumSet.of(PrimitiveType.Date, PrimitiveType.Time, PrimitiveType.DateTime);
    private static final EnumSet<PrimitiveType> SUPPORTED_TEMPORAL_AMOUNT_TYPES = EnumSet.of(PrimitiveType.Duration);

    private final Archetype archetype;

    private BinaryBooleanOperandEvaluator booleanOperandEvaluator = new BinaryBooleanOperandEvaluator(this);
    private BinaryStringOperandEvaluator stringOperandEvaluator = new BinaryStringOperandEvaluator(this);
    private BinaryTemporalOperandEvaluator temporalOperandEvaluator = new BinaryTemporalOperandEvaluator(this);
    private BinaryTemporalAmountOperandEvaluator temporalAmountOperandEvaluator = new BinaryTemporalAmountOperandEvaluator(this);

    private final ModelInfoLookup lookup; //for now only the archie rm model for rule evaluation

    public BinaryOperatorEvaluator(ModelInfoLookup lookup, Archetype archetype) {
        this.lookup = lookup;
        this.archetype = archetype;
    }

    @Override
    public ValueList evaluate(RuleEvaluation<?> evaluation, BinaryOperator statement) {
        switch(statement.getOperator()) {
            case plus:
            case minus:
            case multiply:
            case divide:
            case modulo:
            case exponent:
                return evaluateArithmeticOperator(evaluation, statement);
            case gt:
            case ge:
            case lt:
            case le:
            case eq:
            case ne:
                return evaluateRelOpOperator(evaluation, statement);
            case and:
            case or:
            case xor:
                return evaluateBooleanOperator(evaluation, statement);
            case matches:
                return evaluateBooleanConstraint(evaluation, statement);
            case implies:
                return evaluateImplies(evaluation, statement);
            //for all is not listed here; it has more information that just two operands, so it has its own evaluator
            //not and exists are unary operators and not handled here
        }
        throw new RuntimeException("operation " + statement.getOperator() + " not yet supported");
    }

    private ValueList evaluateImplies(RuleEvaluation<?> evaluation, BinaryOperator statement) {
        ValueList leftValue = evaluation.evaluate(statement.getLeftOperand());
        ValueList rightValue = evaluation.evaluate(statement.getRightOperand());
        if(leftValue.getSingleBooleanResult()) {
            return rightValue;
        } else {
            //if the left operand evaluates to false, this implies nothing and the result is true
            return new ValueList(true, leftValue.getAllPaths());
        }
    }

    private ValueList evaluateBooleanConstraint(RuleEvaluation<?> evaluation, BinaryOperator statement) {
        ValueList leftValues = evaluation.evaluate(statement.getLeftOperand());
        if(!(statement.getRightOperand() instanceof Constraint)){
            throw new IllegalArgumentException("cannot evaluate matches statement, right operand not a constraint");
        }
        Constraint<?> constraint = (Constraint<?>) statement.getRightOperand();
        ValueList result = new ValueList();
        result.setType(PrimitiveType.Boolean);
        DummyRulesPrimitiveObjectParent dummyParent = null;
        if(constraint.getItem() instanceof CTerminologyCode) {
            constraint = (Constraint) constraint.clone();
            //hack to support CTerminologyConstraints properly
            dummyParent = new DummyRulesPrimitiveObjectParent(archetype);
            constraint.getItem().setParent(dummyParent);
        }
        for(Value<?> value:leftValues.getValues()) {
            if(dummyParent != null && !value.getPaths().isEmpty()) {
                //set the path where this thing came from, so the terminology code can be matched
                //against the correct part of the archetype. This is a bit of a hack
                //better would be to flatten rules in a separate structure for each included archetype, similar to componentTerminologies.
                //and to evaluate those. That is a later improvement than this fix
                dummyParent.setPathSegments(convertToArchetypePath((String) value.getPaths().get(0)));
            }
            result.addValue(constraint.getItem().isValidValue(lookup, value.getValue()), value.getPaths());
        }
        return result;

    }

    private List<PathSegment> convertToArchetypePath(String path) {
        List<PathSegment> segments = new APathQuery(path).getPathSegments();
        for(PathSegment segment:segments) {
            if(segment.getIndex() != null) {
                segment.setIndex(null);//no indices in archetype paths
            }
        }
        String archetypePath = segments.isEmpty() ? "/" : Joiner.on("").join(segments);
        List<ArchetypeModelObject> allMatchingPredicate = new AOMPathQuery(archetypePath).findAllMatchingPredicate(archetype.getDefinition(), o -> true);
        if(allMatchingPredicate.isEmpty()) {
            return segments;
        } else {
            ArchetypeModelObject archetypeModelObject = allMatchingPredicate.get(allMatchingPredicate.size() - 1);
            if(archetypeModelObject instanceof ArchetypeConstraint) {
                return ((ArchetypeConstraint) archetypeModelObject).getPathSegments();
            }
            return segments;
        }
    }

    private ValueList evaluateBooleanOperator(RuleEvaluation<?> evaluation, BinaryOperator statement) {

        ValueList leftValues = evaluation.evaluate(statement.getLeftOperand());
        ValueList rightValues = evaluation.evaluate(statement.getRightOperand());

        if(statement.getOperator() != OperatorKind.or) {
            ValueList possibleNullResult = checkAndHandleNull(leftValues, rightValues);
            if (possibleNullResult != null) {
                possibleNullResult.setType(PrimitiveType.Boolean);
                return possibleNullResult;
            }
        }

        checkisBoolean(leftValues, rightValues);

        ValueList result = new ValueList();
        result.setType(PrimitiveType.Boolean);
        if(leftValues.size() == rightValues.size()) {
            for(int i = 0; i < leftValues.size();i++) {
                Value<Boolean> leftValue = (Value<Boolean>) leftValues.get(i);
                Value<Boolean> rightValue = (Value<Boolean>) rightValues.get(i);
                List<String> paths = getPaths(leftValue, rightValue);
                result.addValue(evaluateBoolean(statement, leftValue.getValue(), rightValue.getValue()), paths);
            }
        } else if (leftValues.size() == 1) {
            Value<Boolean> leftValue = (Value<Boolean>) leftValues.get(0);
            if (rightValues.isEmpty()) {
                result.addValue(evaluateBoolean(statement, leftValue.getValue(), null), leftValue.getPaths());
            }
            for(Value<?> rightValue:rightValues.getValues()) {
                List<String> paths = getPaths(leftValue, rightValue);
                result.addValue(evaluateBoolean(statement, leftValue.getValue(), ((Value<Boolean>) rightValue).getValue()), paths);
            }
        } else if (rightValues.size() == 1) {
            Value<Boolean>  rightValue = (Value<Boolean>) rightValues.get(0);
            if (leftValues.isEmpty()) {
                result.addValue(evaluateBoolean(statement, null, rightValue.getValue()), rightValue.getPaths());
            }
            for(Value<?> leftValue:leftValues.getValues()) {
                List<String> paths = getPaths(leftValue, rightValue);
                result.addValue(evaluateBoolean(statement, ((Value<Boolean>) leftValue).getValue(), rightValue.getValue()), paths);
            }
        } else {
            throw new IllegalArgumentException("sizes of operator arguments not compatible");
        }

        return result;
    }

    protected List<String> getPaths(Value<?> leftValue, Value<?> rightValue) {
        List<String> paths = new ArrayList<>();
        paths.addAll(leftValue.getPaths());
        paths.addAll(rightValue.getPaths());
        return paths;
    }

    private Boolean evaluateBoolean(BinaryOperator statement, Boolean leftBoolean, Boolean rightBoolean) {
        switch(statement.getOperator()) {
            case and:
                return leftBoolean & rightBoolean;
            case or:
                if(leftBoolean == null && rightBoolean == null) {
                    return null;
                } else if (leftBoolean == null) {
                    if(rightBoolean) {
                        return rightBoolean;
                    }
                    return null;
                } else if (rightBoolean == null) {
                    if(leftBoolean) {
                        return leftBoolean;
                    }
                    return null;
                }
                return leftBoolean | rightBoolean;
            case xor:
                return leftBoolean ^ rightBoolean;
            default:
                throw new IllegalArgumentException("Not a boolean operator: " + statement.getOperator());
        }
    }

    private ValueList evaluateArithmeticOperator(RuleEvaluation<?> evaluation, BinaryOperator statement) {
        ValueList leftValues = evaluation.evaluate(statement.getLeftOperand());
        ValueList rightValues = evaluation.evaluate(statement.getRightOperand());


        ValueList possibleNullResult = checkAndHandleNull(leftValues, rightValues);
        if(possibleNullResult != null) {
            possibleNullResult.setType(PrimitiveType.Real);
            return possibleNullResult;
        } else {
            ValueList result = new ValueList();
            result.setType(PrimitiveType.Real);

            checkIsNumber(leftValues, rightValues);
            if (leftValues.size() == rightValues.size()) {
                for (int i = 0; i < leftValues.size(); i++) {
                    Value<?> leftValue = leftValues.get(i);
                    Value<?> rightValue = rightValues.get(i);
                    evaluateArithmetic(statement, result, rightValue.getValue(), leftValue.getValue(), getPaths(leftValue, rightValue));
                }
            } else if (leftValues.size() == 1) {
                Value<?> leftValue = leftValues.get(0);
                for (Value<?> rightValue : rightValues.getValues()) {
                    evaluateArithmetic(statement, result, rightValue.getValue(), leftValue.getValue(), getPaths(leftValue, rightValue));
                }
            } else if (rightValues.size() == 1) {
                Value<?> rightValue = rightValues.get(0);
                for (Value<?> leftValue : leftValues.getValues()) {
                    evaluateArithmetic(statement, result, rightValue.getValue(), leftValue.getValue(), getPaths(leftValue, rightValue));
                }
            } else {
                //TODO: this also happens when one of the modelreferences has a value that does not exist in the model, but others have - while the rules are correct. Those are very valid cases
                //how to fix?
                throw new IllegalArgumentException("sizes of operator arguments not compatible");
            }
            return result;
        }

    }

    private void evaluateArithmetic(BinaryOperator statement, ValueList result, Object rightValue, Object leftValue, List<String> paths) {
        if(leftValue instanceof Long && rightValue instanceof Long) {
            result.addValue(evaluateIntegerArithmetic(statement.getOperator()
                    , (Long) leftValue
                    , (Long) rightValue), paths);
            result.setType(PrimitiveType.Integer);
        } else {
            result.addValue(evaluateRealArithmetic(statement.getOperator()
                            , convertToDouble(leftValue)
                            , convertToDouble(rightValue))
                    , paths
            );
            result.setType(PrimitiveType.Real);
        }
    }

    private Double evaluateRealArithmetic(OperatorKind operator, Double leftNumber, Double rightNumber) {
        switch(operator) {
            case plus:
                return leftNumber + rightNumber;
            case minus:
                return leftNumber - rightNumber;
            case multiply:
                return leftNumber * rightNumber;
            case divide:
                return leftNumber / rightNumber;
            case modulo:
                return leftNumber % rightNumber;
            case exponent:
                return Math.pow(leftNumber, rightNumber);
            default:
                throw new IllegalArgumentException("Not an arithmetic operator: " + operator);
        }
    }



    private Long evaluateIntegerArithmetic(OperatorKind operator, Long leftNumber, Long rightNumber) {
        switch(operator) {
            case plus:
                return leftNumber + rightNumber;
            case minus:
                return leftNumber - rightNumber;
            case multiply:
                return leftNumber * rightNumber;
            case divide:
                return leftNumber / rightNumber;
            case modulo:
                return leftNumber % rightNumber;
            case exponent:
                return (long) Math.pow(leftNumber, rightNumber);
            default:
                throw new IllegalArgumentException("Not an arithmetic operator: " + operator);
        }
    }

    private ValueList evaluateRelOpOperator(RuleEvaluation<?> evaluation, BinaryOperator statement) {
        ValueList leftValues = evaluation.evaluate(statement.getLeftOperand());
        ValueList rightValues = evaluation.evaluate(statement.getRightOperand());

        ValueList possibleNullResult = handlePossibleNullRelOpResult(statement, leftValues, rightValues);
        if(possibleNullResult != null) {
            possibleNullResult.setType(PrimitiveType.Boolean);
            return possibleNullResult;
        } else if(leftValues.getType() == PrimitiveType.Boolean || rightValues.getType() == PrimitiveType.Boolean) {
            //TODO: check types and throw error when one of the types is not either boolean or null
            if(!EnumSet.of(OperatorKind.eq, OperatorKind.ne).contains(statement.getOperator()) ) {
                throw new IllegalStateException("Operator " + statement.getOperator().toString() + " not valid for boolean type");
            }
            ValueList result = new ValueList();
            result.setType(PrimitiveType.Boolean);

            //according to the xpath spec, at least one pair from both collections must exist that matches the condition.
            //want otherwise? Use for_all/every
            result.addValue(booleanOperandEvaluator.evaluateMultipleValuesBooleanRelOp(statement, leftValues, rightValues));

            return result;

        } else if (leftValues.getType() == PrimitiveType.String || rightValues.getType() == PrimitiveType.String) {
            //TODO: check types and throw error when one of the types is not either boolean or null
            if(!EnumSet.of(OperatorKind.eq, OperatorKind.ne).contains(statement.getOperator()) ) {
                throw new IllegalStateException("Operator " + statement.getOperator().toString() + " not valid for boolean type");
            }
            ValueList result = new ValueList();
            result.setType(PrimitiveType.Boolean);

            //according to the xpath spec, at least one pair from both collections must exist that matches the condition.
            //want otherwise? Use for_all/every
            result.addValue(stringOperandEvaluator.evaluateMultipleValuesStringRelOp(statement, leftValues, rightValues));

            return result;
        } else if (SUPPORTED_TEMPORAL_TYPES.contains(leftValues.getType()) || SUPPORTED_TEMPORAL_TYPES.contains(rightValues.getType())) {
            ValueList result = new ValueList();
            result.setType(PrimitiveType.Boolean);

            //according to the xpath spec, at least one pair from both collections must exist that matches the condition.
            //want otherwise? Use for_all/every
            result.addValue(temporalOperandEvaluator.evaluateMultipleValuesDateRelOp(statement, leftValues, rightValues));

            return result;
        } else if (SUPPORTED_TEMPORAL_AMOUNT_TYPES.contains(leftValues.getType()) || SUPPORTED_TEMPORAL_AMOUNT_TYPES.contains(rightValues.getType())) {
            ValueList result = new ValueList();
            result.setType(PrimitiveType.Boolean);

            //according to the xpath spec, at least one pair from both collections must exist that matches the condition.
            //want otherwise? Use for_all/every
            result.addValue(temporalAmountOperandEvaluator.evaluateMultipleValuesDateRelOp(statement, leftValues, rightValues));

            return result;
        } else {
            checkIsNumberOrNull(leftValues, rightValues);

            ValueList result = new ValueList();
            result.setType(PrimitiveType.Boolean);

            //according to the xpath spec, at least one pair from both collections must exist that matches the condition.
            //want otherwise? Use for_all/every
            result.addValue(evaluateMultipleValuesRelOp(statement, leftValues, rightValues));

            return result;
        }
    }

    private ValueList handlePossibleNullRelOpResult(BinaryOperator statement, ValueList leftValues, ValueList rightValues) {
        switch(statement.getOperator()) {
            case eq:
            case ne:
                //if null, can be true or false depending on checks
                return null;
            case gt:
            case ge:
            case lt:
            case le:
                //if null, result undefined, return null
                return checkAndHandleNull(leftValues, rightValues);
            default: throw new IllegalStateException("unknown relop operator");
        }

    }

    private Value<?> evaluateMultipleValuesRelOp(BinaryOperator statement, ValueList leftValues, ValueList rightValues) {

        for(Value<?> leftValue:leftValues.getValues()) {
            for (Value<?> rightValue:rightValues.getValues()) {
                Value<?> evaluatedRelOp = evaluateRelOp(statement, leftValue.getValue(), rightValue.getValue(), getPaths(leftValue, rightValue));
                if (((Boolean) evaluatedRelOp.getValue()).booleanValue()) {
                    return evaluatedRelOp;
                }
            }
        }
        return new Value<>(false, getAllPaths(leftValues, rightValues));
    }

    protected List<String> getAllPaths(ValueList leftValue, ValueList rightValue) {
        List<String> allPaths = new ArrayList<>();
        allPaths.addAll(leftValue.getAllPaths());
        allPaths.addAll(rightValue.getAllPaths());
        return allPaths;
    }

    private Value<?> evaluateRelOp(BinaryOperator statement, Object leftValue, Object rightValue, List<String> paths) {
        if(leftValue == null || rightValue == null) {
            return new Value<>(evaluateNullRelOp(statement.getOperator(), leftValue, rightValue), paths);
        }
        else if(leftValue instanceof Long && rightValue instanceof Long) {
            return new Value<>(evaluateIntegerRelOp(statement.getOperator(),
                    (Long) leftValue,
                    (Long) rightValue
            ), paths);
        } else {
            return new Value<>(evaluateRealRelOp(statement.getOperator(),
                    convertToDouble(leftValue),
                    convertToDouble(rightValue)
            ), paths);
        }
    }

    protected Boolean evaluateNullRelOp(OperatorKind operator, Object leftValue, Object rightValue) {
        switch(operator) {
            case eq:
                if(leftValue == null && rightValue == null) {
                    return true;
                }
                if(leftValue == null && rightValue != null || leftValue != null && rightValue == null) {
                    return false;
                }
            case ne:
                if(leftValue == null && rightValue == null) {
                    return false;
                }
                if(leftValue == null && rightValue != null || leftValue != null && rightValue == null) {
                    return true;
                }
            default:
                return null;
        }
    }

    private Boolean evaluateIntegerRelOp(OperatorKind operator, long leftNumber, long rightNumber) {

        switch(operator) {
            case eq:
                return leftNumber == rightNumber;
            case ne:
                return leftNumber != rightNumber;
            case gt:
                return leftNumber > rightNumber;
            case lt:
                return leftNumber < rightNumber;
            case ge:
                return leftNumber >= rightNumber;
            case le:
                return leftNumber <= rightNumber;

            default:
                throw new IllegalArgumentException("Not a boolean operator: " + operator);
        }
    }

    private Boolean evaluateRealRelOp(OperatorKind operator, Double leftNumber, Double rightNumber) {

        switch(operator) {
            case eq:
                return Math.abs(leftNumber - rightNumber) < EPSILON;
            case ne:
                return Math.abs(leftNumber - rightNumber) >= EPSILON;
            case gt:
                return leftNumber > rightNumber;
            case lt:
                return leftNumber < rightNumber;
            case ge:
                return leftNumber >= rightNumber;
            case le:
                return leftNumber <= rightNumber;
            default:
                throw new IllegalArgumentException("Not a boolean operator: " + operator);
        }
    }

    public static void checkisBoolean(ValueList leftValueList, ValueList rightValueList) {
        EnumSet<PrimitiveType> booleanTypes = EnumSet.of(PrimitiveType.Boolean);
        if(!booleanTypes.contains(leftValueList.getType())) {
            throw new RuntimeException("not a boolean with boolean operator: " + leftValueList.getType());//TODO: proper errors
        }
        if(!booleanTypes.contains(rightValueList.getType())) {
            throw new RuntimeException("not a boolean with boolean operator: " + rightValueList.getType());//TODO: proper errors
        }
    }

    public static  void checkIsNumberOrNull(ValueList leftValueList, ValueList rightValueList) {
        if(leftValueList.isEmpty() || rightValueList.isEmpty()) {
            return;
        }
        EnumSet<PrimitiveType> numberTypes = EnumSet.of(PrimitiveType.Integer, PrimitiveType.Real);
        if(!numberTypes.contains(leftValueList.getType())) {
            throw new RuntimeException("Type supplied to operator should be a number, but it is not: " + leftValueList.getType());//TODO: proper errors
        }
        if(!numberTypes.contains(rightValueList.getType())) {
            throw new RuntimeException("Type supplied to operator should be a number, but it is not: " + rightValueList.getType());//TODO: proper errors
        }
    }

    public static void checkIsNumber(ValueList leftValueList, ValueList rightValueList) {
        EnumSet<PrimitiveType> numberTypes = EnumSet.of(PrimitiveType.Integer, PrimitiveType.Real);
        if(!numberTypes.contains(leftValueList.getType())) {
            throw new RuntimeException("Type supplied to operator should be a number, but it is not: " + leftValueList.getType());//TODO: proper errors
        }
        if(!numberTypes.contains(rightValueList.getType())) {
            throw new RuntimeException("Type supplied to operator should be a number, but it is not: " + rightValueList.getType());//TODO: proper errors
        }
    }

    private double convertToDouble(Object value) {
        if(value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new IllegalArgumentException("Can only convert Numbers to double values");
        }
    }

    @Override
    public List<Class<?>> getSupportedClasses() {
        return Lists.newArrayList(BinaryOperator.class);
    }
}
