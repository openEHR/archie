package com.nedap.archie.rm.support.identification;

/**
 * Created by pieter.bos on 08/07/16.
 */
public class GenericId extends ObjectId {

    private String scheme;

    public GenericId() {
    }

    public GenericId(String value, String scheme) {
        super(value);
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GenericId genericId = (GenericId) o;

        return scheme != null ? scheme.equals(genericId.scheme) : genericId.scheme == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        return result;
    }
}
