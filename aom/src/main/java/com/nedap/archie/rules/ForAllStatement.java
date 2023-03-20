package com.nedap.archie.rules;

import java.util.Objects;

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

    public void setPathExpression(Expression pathExpression) {
        super.setFirstOperand(pathExpression);
    }

    public Expression getAssertion() {
        return getSecondOperand();
    }

    public void setAssertion(Expression assertion) {
        super.setSecondOperand(assertion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForAllStatement)) return false;
        if (!super.equals(o)) return false;
        ForAllStatement that = (ForAllStatement) o;
        return Objects.equals(variableName, that.variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), variableName);
    }
}
