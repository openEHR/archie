package com.nedap.archie.rules;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class Operator extends Expression {

    private OperatorKind operator;

    private List<Expression> operands = new ArrayList<>();

    private String symbol;

    @JsonIgnore
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

    @JsonIgnore
    protected Expression getFirstOperand() {
        return operands.size() > 0 ? operands.get(0) : null;
    }

    protected void setFirstOperand(Expression firstOperand) {
        setLeftOperand(firstOperand);
    }

    @JsonIgnore
    protected Expression getSecondOperand() {
        return operands.size() > 1 ? operands.get(1) : null;
    }

    protected void setSecondOperand(Expression secondOperand) {
        setRightOperand(secondOperand);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public OperatorDef getOperatorDef() {
        return operator == null ? null : new OperatorDefBuiltin(operator.getIdentifier());
    }

    public void setOperatorDef(OperatorDef operatorDef) {
        if(operatorDef != null) {
            if(operatorDef.getIdentifier() != null) {
                operator = OperatorKind.parseFromIdentifier(operatorDef.getIdentifier());
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

    @JsonIgnore //this field should not be in the json
    public boolean isUnary() {
        return operands.size() == 1;
    }

    public void addOperand(Expression expression) {
        operands.add(expression);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Operator)) return false;
        if (!super.equals(o)) return false;
        Operator operator1 = (Operator) o;
        return operator == operator1.operator &&
                Objects.equals(operands, operator1.operands) &&
                Objects.equals(symbol, operator1.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                operator,
                operands,
                symbol);
    }
}
