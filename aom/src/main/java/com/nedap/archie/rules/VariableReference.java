package com.nedap.archie.rules;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariableReference)) return false;
        if (!super.equals(o)) return false;
        VariableReference that = (VariableReference) o;
        return Objects.equals(declaration, that.declaration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), declaration);
    }
}
