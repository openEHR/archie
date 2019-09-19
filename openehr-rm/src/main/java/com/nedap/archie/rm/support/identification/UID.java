package com.nedap.archie.rm.support.identification;

import com.nedap.archie.rm.RMObject;

import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
public abstract class UID extends RMObject {

    String value;

    public UID() {
    }

    public UID(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UID uid = (UID) o;
        return Objects.equals(value, uid.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
