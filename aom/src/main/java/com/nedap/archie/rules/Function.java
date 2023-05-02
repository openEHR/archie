package com.nedap.archie.rules;

import java.util.List;

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
}
