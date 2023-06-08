package com.nedap.archie.rules;

import java.util.Objects;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class ModelReference extends Leaf {

    /**
     * The path can be prefixed with a variable, referencing another path.
     * For example 'every $event in /data/events satisfies $event/value &gt; 5'
     */
    private String variableReferencePrefix;

    private String path;

    public ModelReference() {

    }

    public String getVariableReferencePrefix() {
        return variableReferencePrefix;
    }

    public ModelReference(String path) {
        this.path = path;
    }

    public ModelReference(String variableReferencePrefix, String path) {
        this.variableReferencePrefix = variableReferencePrefix;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String toString() {
        if(variableReferencePrefix == null) {
            return path;
        } else {
            return "$" + variableReferencePrefix + path;
        }
    }

    public void setVariableReferencePrefix(String variableReferencePrefix) {
        this.variableReferencePrefix = variableReferencePrefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelReference)) return false;
        if (!super.equals(o)) return false;
        ModelReference that = (ModelReference) o;
        return Objects.equals(variableReferencePrefix, that.variableReferencePrefix)
                && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                variableReferencePrefix,
                path);
    }
}
