package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class Constant<T> extends Leaf {

    private T value;

    public Constant() {

    }

    public Constant(ExpressionType type, T value) {
        setType(type);
        setValue(value);
    }


    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
