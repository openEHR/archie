package com.nedap.archie.rules;


import com.nedap.archie.aom.CPrimitiveObject;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class Constraint<T extends CPrimitiveObject<?, ?>> extends Leaf {

    private T item;

    public Constraint() {

    }

    public Constraint(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
