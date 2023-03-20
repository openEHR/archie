package com.nedap.archie.rules;

import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 06/04/2017.
 */
public class Function extends Expression {
    private String functionName;
    private List<Expression> arguments;

    /** No argument constructor for kryo cloning and json parsing */
    public Function() {

    }

    public Function(String functionName, List<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Function)) return false;
        if (!super.equals(o)) return false;
        Function function = (Function) o;
        return Objects.equals(functionName, function.functionName) && Objects.equals(arguments, function.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), functionName, arguments);
    }
}
