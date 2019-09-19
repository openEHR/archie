package com.nedap.archie.rm.support.identification;

import com.nedap.archie.rm.RMObject;

import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
public class VersionTreeId extends RMObject {

    private String value;

    public VersionTreeId() {
    }

    public VersionTreeId(String value) {
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
        VersionTreeId that = (VersionTreeId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
