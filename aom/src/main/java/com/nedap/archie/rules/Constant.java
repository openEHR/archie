package com.nedap.archie.rules;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constant)) return false;
        if (!super.equals(o)) return false;
        Constant<?> constant = (Constant<?>) o;
        return Objects.equals(value, constant.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
