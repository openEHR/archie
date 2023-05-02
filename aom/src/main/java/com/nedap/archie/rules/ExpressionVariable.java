package com.nedap.archie.rules;

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

}
