package com.nedap.archie.rules;

import com.nedap.archie.aom.primitives.CString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Assertion object.
 * Created by pieter.bos on 15/10/15.
 */
public final class Assertion extends RuleStatement {

    private String stringExpression;

    private Expression expression;
    @Nullable
    private String tag;

    private List<VariableDeclaration> variables = new ArrayList<>();

    public Assertion() {

    }

    /**
     * Set the string expression. WARNING: this will not set the other fields, this is not a parser.
     * @param text
     */
    public Assertion(String text) {
        this.stringExpression = text;
    }

    public String getStringExpression() {
        return stringExpression;
    }

    /**
     * Set the string expression. WARNING: this will not set the other fields, this is not a parser.
     * @param text
     */
    public void setStringExpression(String text) {
        this.stringExpression = text;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public List<VariableDeclaration> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableDeclaration> variables) {
        this.variables = variables;
    }

    public void addVariable(VariableDeclaration variable) {
        variables.add(variable);
    }

    public boolean matchesAny() {
        if(expression instanceof BinaryOperator) {
            BinaryOperator binaryOperator = (BinaryOperator) expression;
            if(binaryOperator.getOperator() == OperatorKind.matches &&
                    binaryOperator.getRightOperand() instanceof Constraint) {
                Constraint<?> constraint = (Constraint<?>) binaryOperator.getRightOperand();
                return constraint.getItem().equals(new CString("/.*/")) ||
                        constraint.getItem().equals(new CString("^.*^"));
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assertion)) return false;
        if (!super.equals(o)) return false;
        Assertion assertion = (Assertion) o;
        return Objects.equals(stringExpression, assertion.stringExpression) && Objects.equals(expression, assertion.expression) && Objects.equals(tag, assertion.tag) && Objects.equals(variables, assertion.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stringExpression, expression, tag, variables);
    }
}
