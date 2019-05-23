package com.nedap.archie.rm.support.identification;

import com.nedap.archie.rm.RMObject;

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

        return value != null ? value.equals(uid.value) : uid.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
