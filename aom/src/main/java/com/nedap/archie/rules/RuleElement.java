package com.nedap.archie.rules;

import com.nedap.archie.aom.ArchetypeModelObject;

import java.util.Objects;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class RuleElement extends ArchetypeModelObject {

    private ExpressionType type;

    public ExpressionType getType() {
        return type;
    }

    public void setType(ExpressionType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleElement)) return false;
        RuleElement that = (RuleElement) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
