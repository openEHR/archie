package com.nedap.archie.rules;

import java.util.Objects;

/**
 * Created by pieter.bos on 27/10/15.
 */
public abstract class Expression extends RuleElement {
    /**
     * If true, this statement originally was placed between ()-signs
     */
    private boolean precedenceOverridden = false;

    public boolean isPrecedenceOverridden() {
        return precedenceOverridden;
    }

    public void setPrecedenceOverridden(boolean precedenceOverridden) {
        this.precedenceOverridden = precedenceOverridden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expression)) return false;
        if (!super.equals(o)) return false;
        Expression that = (Expression) o;
        return precedenceOverridden == that.precedenceOverridden;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), precedenceOverridden);
    }
}
