package com.nedap.archie.rules.evaluation;

import com.google.common.collect.ArrayListMultimap;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.ForAllStatement;
import com.nedap.archie.rules.ModelReference;
import com.nedap.archie.rules.OperatorKind;
import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.RuleElement;
import com.nedap.archie.rules.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Checks evaluated assertions on what can be fixed automatically, and how this can be done.
 *
 * Created by pieter.bos on 18/05/16.
 */
class FixableAssertionsChecker {

    private static Logger logger = LoggerFactory.getLogger(FixableAssertionsChecker.class);

    private ArrayListMultimap<RuleElement, ValueList> ruleElementValues;

    private VariableMap forAllVariables;

    protected FixableAssertionsChecker(ArrayListMultimap<RuleElement, ValueList> ruleElementValues) {
        this.ruleElementValues = ruleElementValues;
        forAllVariables = new VariableMap();
    }

    /**
     * Check the assertion result for patterns that can be automatically fixed so they evaluate to true. Currently the following patterns can be automatically fixed:
     *
     * /path/value = ...expression...
     * .... implies _another matched pattern_
     * exists .....
     * not exists ....
     * for all .. in ... satisfies _another matched pattern
     *
     *
     * @param assertionResult the evaluationresult to set the fix instructions to
     * @param expression the expression to be evaluated
     @param expression the current index, if in a for all expression. 0 (-1? TODO) otherwise
     */
    protected void checkAssertionForFixablePatterns(AssertionResult assertionResult, Expression expression, int index) {

        if(expression instanceof ForAllStatement) {
            handleForAll(assertionResult, expression);

        } else if (expression instanceof BinaryOperator) {
            ValueList expressionResult = ruleElementValues.get(expression).get(index);
            BinaryOperator binaryExpression = (BinaryOperator) expression;
            if (binaryExpression.getOperator() == OperatorKind.eq && binaryExpression.getLeftOperand() instanceof ModelReference) {
                handlePathEquals(assertionResult, expressionResult, binaryExpression, index);
            } else if (binaryExpression.getOperator() == OperatorKind.implies) {
                handleImplies(assertionResult, index, binaryExpression);
            } else if (binaryExpression.getOperator() == OperatorKind.matches) {
                handleMatches(assertionResult, index, binaryExpression);
            } else if (binaryExpression.getOperator() == OperatorKind.and) {
                checkAssertionForFixablePatterns(assertionResult, binaryExpression.getLeftOperand(), index);
                checkAssertionForFixablePatterns(assertionResult, binaryExpression.getRightOperand(), index);
            }
        } else if (expression instanceof UnaryOperator) {
            UnaryOperator unaryOperator = (UnaryOperator) expression;
            if(unaryOperator.getOperator() == OperatorKind.not) {
                handleNot(assertionResult, unaryOperator, index);
            }
            if (unaryOperator.getOperator() == OperatorKind.exists) {
                //TODO exists expressions
                if (unaryOperator.getOperand() instanceof ModelReference) { //TODO: this could also be an objectreference
                    //matches exists /path/to/value
                    //TODO: this shows that a specific archetype path must exist. But it could just as well be a path within a specific node. So find a way to get the RM Path
                    //pointing to the right node here
                    assertionResult.addPathThatMustExist(resolveModelReference((ModelReference) unaryOperator.getOperand()));
                }
            }
        }

        //TODO: not expressions, reversing the expected value?
    }



    private void handleNot(AssertionResult assertionResult, UnaryOperator unaryOperator, int index) {
        Expression operand = unaryOperator.getOperand();
        if(operand instanceof UnaryOperator && ((UnaryOperator) operand).getOperator() == OperatorKind.exists) {
            UnaryOperator existsOperator = (UnaryOperator) operand;
            if(existsOperator.getOperand() instanceof  ModelReference) { //TODO: this could also be an objectreference
                //matches exists /path/to/value
                List<ValueList> valueLists = ruleElementValues.get(existsOperator);
                ValueList list = valueLists.get(index);
                if(list.getSingleBooleanResult()) { //it exists. It should not
                    assertionResult.addPathsThatMustNotExist(resolveModelReferenceNonNull((ModelReference) existsOperator.getOperand(), index));
                } else { //does not exist, it's fine but we should still know. We need another model reference lookup method
                    assertionResult.addPathThatMustNotExist(resolveModelReference((ModelReference) existsOperator.getOperand()));
                }

            }
        }
    }

    private void handleImplies(AssertionResult assertionResult, int index, BinaryOperator binaryExpression) {
        //matches ... implies ...
        ValueList leftOperandResult = ruleElementValues.get(binaryExpression.getLeftOperand()).get(index);
        if(!leftOperandResult.isEmpty()) { //if the left operand cannot be evaluated, do not attempt to fix anything
            boolean shouldEvaluate = leftOperandResult.getSingleBooleanResult();
            if (shouldEvaluate) {
                checkAssertionForFixablePatterns(assertionResult, binaryExpression.getRightOperand(), index);
            }
        }
    }

    private void handlePathEquals(AssertionResult assertionResult, ValueList expressionResult, BinaryOperator binaryExpression, int index) {
        //matches the form /path/to/something = 3 + 5 * /value[id23]
        ValueList valueList = this.ruleElementValues.get(binaryExpression.getRightOperand()).get(index);
        ModelReference pathToSet = (ModelReference) binaryExpression.getLeftOperand();
        setPathsToValues(assertionResult, resolveModelReference(pathToSet), valueList);
    }


    private void handleMatches(AssertionResult assertionResult, int index, BinaryOperator binaryExpression) {
        // matches the form '/path matches {|at6|}' - only doing something is the constraint matches a constant value
        Expression rightOperand = binaryExpression.getRightOperand();
        ModelReference pathToSet = (ModelReference) binaryExpression.getLeftOperand();
        if(rightOperand instanceof Constraint) {
            Constraint<?> c = (Constraint<?>) rightOperand;
            CPrimitiveObject<?, ?> object = c.getItem();
            List<?> constraints = object.getConstraint();
            if(constraints.size() != 1) {
                return;
            }
            ValueList valueList = new ValueList();
            switch(object.getClass().getSimpleName()) {
                case "CTerminologyCode": {
                    String constraint = (String) constraints.get(0);
                    if (AOMUtils.isValueCode(constraint)) {
                        valueList.addValue(constraint, Collections.emptyList());
                        String path = resolveModelReference(pathToSet);
                        if (path.endsWith("symbol")) { // different for DV_CODED_TEXT and DV_CODEPHRASE
                            /// it would be better to check the actual type, but hasn't been done now
                            //this limits this to the OpenEHR RM
                            path = path + "/defining_code/code_string";
                        } else {
                            path = path + "/code_string";
                        }
                        setPathsToValues(assertionResult, path, valueList);
                    } else if (AOMUtils.isValueSetCode(constraint)) {
                        String path = resolveModelReference(pathToSet);
                        assertionResult.constrainPathToValueSet(path, constraint);
                    }
                    break;
                }
                case "CString": {
                        String constraint = (String) constraints.get(0);
                        valueList.addValue(constraint, Collections.emptyList());
                        setPathsToValues(assertionResult, resolveModelReference(pathToSet), valueList);
                    }
                    break;
                //TODO: more type of constraints

            }


        } else {
            //cannot be evaluated?
        }

    }


    private void handleForAll(AssertionResult assertionResult, Expression expression) {
        //this handles forAll ..., even with an extra for all.
        //TODO: the nested loop forall .. in .. forall .. in .. will not yet work due to problems with the ruleElementValues. Refactor!
        ForAllStatement forAllStatement = (ForAllStatement) expression;
        Collection<ValueList> valueLists = ruleElementValues.get(forAllStatement.getAssertion());
        int i = 0;

        ValueList pathExpressionValues = ruleElementValues.get(forAllStatement.getPathExpression()).get(0);
        for(ValueList valueList:valueLists) {
            //if(!valueList.getSingleBooleanResult()) {
            //TODO: this code is a bit hard to understand

            //set the variables to what they were during the for all evaluation.
            //this is a bit of code duplication from the ForAllEvaluator. I think improvement is possible in this place
            Value<?> value = pathExpressionValues.get(i);
            Object context = value.getValue();
            String path = (String) value.getPaths().get(0);

            // according to the latest openEHR docs, this should be 'objectreference'.
            // We could change the name of the java class

            RMObjectWithPath rmObjectWithPath = new RMObjectWithPath(context, path);
            ValueList variableValue = new ValueList(rmObjectWithPath);
            variableValue.setType(PrimitiveType.ObjectReference);

            forAllVariables.put(forAllStatement.getVariableName(), variableValue);
            checkAssertionForFixablePatterns(assertionResult, forAllStatement.getAssertion(), i);
            i++;
        }
        forAllVariables.put(forAllStatement.getVariableName(), null);
    }

    private String resolveModelReference(ModelReference statement) {
        String variable = statement.getVariableReferencePrefix();
        String pathPrefix = "";
        if(variable != null) {
            //resolve variable and add path prefix
            ValueList value = forAllVariables.get(variable);
            if(value.size() > 1) {
                throw new IllegalStateException("");
            } else if (value.size() == 1) {
                if(value.getType() == PrimitiveType.ObjectReference) {
                    RMObjectWithPath reference = (RMObjectWithPath) value.get(0).getValue();
                    pathPrefix = reference.getPath();
                } else {
                    //TODO: this is not correct
                    if(value.get(0).getPaths().size() > 1) {
                        throw new IllegalStateException("");
                    }

                    pathPrefix = (String) value.get(0).getPaths().get(0);
                }

            } //0: do nothing, empty value, no path prefix
        }

        return pathPrefix + statement.getPath();
    }

    private List<String> resolveModelReferenceNonNull(ModelReference statement, int index) {

        List<ValueList> values = ruleElementValues.get(statement);
        if(index > values.size()) {
            return Collections.emptyList();
        } else {
            ValueList valueList = values.get(index);
            return valueList.getAllPaths();
        }

    }

    private void setPathsToValues(AssertionResult assertionResult, String path, ValueList value) {
        logger.debug("path {} set to value {} ", path, value);
        assertionResult.setSetPathValue(path, value);
    }
}
