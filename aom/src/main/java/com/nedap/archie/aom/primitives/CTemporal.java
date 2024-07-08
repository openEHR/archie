package com.nedap.archie.aom.primitives;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * TODO: cConformsTo for temporal and date types
 * Created by pieter.bos on 15/10/15.
 */
public abstract class CTemporal<T> extends COrdered<T>{

    @JsonAlias("patterned_constraint")
    private String patternConstraint;

    public String getPatternConstraint() {
        return patternConstraint;
    }

    public void setPatternConstraint(String patternConstraint) {
        this.patternConstraint = patternConstraint;
    }

    @Override
    @Deprecated
    public boolean isValidValue(T value) {
        if(getConstraint().isEmpty() && patternConstraint == null) {
            return true;
        }
        if(patternConstraint == null) {
            return super.isValidValue(value);
        } else {
            //TODO: find a library that validates ISO 8601 patterns
            return true;
        }
    }
}
