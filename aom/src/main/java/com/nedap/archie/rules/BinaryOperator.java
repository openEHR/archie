package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class BinaryOperator extends Operator {

    public BinaryOperator() {

    }

    public BinaryOperator(ExpressionType type, OperatorKind operator, String operatorSymbol, Expression leftOperand, Expression rightOperand) {
        setType(type);
        setOperator(operator);
        setSymbol(operatorSymbol);
        addOperand(leftOperand);
        addOperand(rightOperand);
    }

    public Expression getLeftOperand() {
        return super.getFirstOperand();
    }

    public Expression getRightOperand() {
        return super.getSecondOperand();
    }
}
