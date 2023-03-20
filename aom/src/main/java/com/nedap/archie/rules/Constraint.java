package com.nedap.archie.rules;


import com.nedap.archie.aom.CPrimitiveObject;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constraint)) return false;
        if (!super.equals(o)) return false;
        Constraint<?> that = (Constraint<?>) o;
        return Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), item);
    }
}
