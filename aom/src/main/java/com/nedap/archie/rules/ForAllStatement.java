package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 10/05/16.
 */
public class ForAllStatement extends Operator {

    private String variableName;

    public ForAllStatement() {

    }

    public ForAllStatement(String variableName, Expression path, Expression assertion) {
        setType(ExpressionType.BOOLEAN);
        setOperator(OperatorKind.for_all);
        addOperand(path);
        addOperand(assertion);

        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public Expression getPathExpression() {
        return getFirstOperand();
    }

    public Expression getAssertion() {
        return getSecondOperand();
    }
}
