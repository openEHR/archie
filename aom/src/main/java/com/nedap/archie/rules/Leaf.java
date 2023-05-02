package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class Leaf extends Expression {

    /* in the docs, this is not a field but only a method. However, just setting this is the most straightforward way, since an expression item*/
    private ReferenceType referenceType;

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }
}
