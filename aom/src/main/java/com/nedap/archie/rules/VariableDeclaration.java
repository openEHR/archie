package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class VariableDeclaration extends RuleStatement {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
