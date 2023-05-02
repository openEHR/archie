package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class VariableReference extends Leaf {
    private VariableDeclaration declaration;

    public VariableDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(VariableDeclaration declaration) {
        this.declaration = declaration;
    }
}
