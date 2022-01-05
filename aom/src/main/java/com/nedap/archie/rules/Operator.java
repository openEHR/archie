package com.nedap.archie.rules;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class Operator extends Expression {

    private OperatorKind operator;

    private List<Expression> operands = new ArrayList<>();

    private String symbol;

    public OperatorKind getOperator() {
        return operator;
    }

    public void setOperator(OperatorKind operator) {
        this.operator = operator;
    }

    @JsonIgnore //the subclasses have the right methods for operands
    public List<Expression> getOperands() {
        return operands;
    }

    public void setOperands(List<Expression> operands) {
        this.operands = operands;
    }

    public Expression getLeftOperand() {
        return operands.size() > 0 ? operands.get(0) : null;
    }

    public Expression getRightOperand() {
        return operands.size() > 1 ? operands.get(1) : null;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public OperatorDef getOperatorDef() {
        return operator == null ? null : new OperatorDef(operator.getIdentifier());
    }

    public void setOperatorDef(OperatorDef operatorDef) {
        if(operatorDef != null) {
            if(operatorDef.getIdentifier() != null) {
                operator = OperatorKind.parse(operatorDef.getIdentifier());
            }
        }
    }

    public void setLeftOperand(Expression operand) {
        if(operands.isEmpty()) {
            operands.add(operand);
        } else {
            operands.set(0, operand);
        }
    }

    public void setRightOperand(Expression operand) {
        if(operands.isEmpty()) {
            operands.add(null);
            operands.add(operand);
        } else if (operands.size() == 1){
            operands.add(operand);
        } else {
            operands.set(1, operand);
        }
    }

    public boolean isUnary() {
        return operands.size() == 1;
    }

    public void addOperand(Expression expression) {
        operands.add(expression);
    }
}
