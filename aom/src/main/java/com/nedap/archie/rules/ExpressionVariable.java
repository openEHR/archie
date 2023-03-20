package com.nedap.archie.rules;

import java.util.Objects;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class ExpressionVariable extends VariableDeclaration {

    private Expression expression;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpressionVariable)) return false;
        if (!super.equals(o)) return false;
        ExpressionVariable that = (ExpressionVariable) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression);
    }
}
