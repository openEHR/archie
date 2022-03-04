package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class UnaryOperator extends Operator {

    public UnaryOperator() {

    }

    public UnaryOperator(ExpressionType type, OperatorKind operator, String operatorSymbol, Expression operand) {
        setType(type);
        setOperator(operator);
        setSymbol(operatorSymbol);
        addOperand(operand);
    }

    public Expression getOperand() {
        return super.getFirstOperand();
    }

}
