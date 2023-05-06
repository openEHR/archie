package com.nedap.archie.rules;

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
}
