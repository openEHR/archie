package com.nedap.archie.aom.primitives;

import javax.xml.bind.annotation.XmlType;

/**
 * TODO: cConformsTo for temporal and date types
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_TEMPORAL")
public abstract class CTemporal<T> extends COrdered<T>{

    private String patternedConstraint;

    public String getPatternedConstraint() {
        return patternedConstraint;
    }

    public void setPatternedConstraint(String patternedConstraint) {
        this.patternedConstraint = patternedConstraint;
    }

    public boolean isValidValue(T value) {
        if(getConstraint().isEmpty() && patternedConstraint == null) {
            return true;
        }
        if(patternedConstraint == null) {
            return super.isValidValue(value);
        } else {
            //TODO: find a library that validates ISO 8601 patterns
            return true;
        }
    }
}
