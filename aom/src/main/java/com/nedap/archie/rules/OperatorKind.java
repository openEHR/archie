package com.nedap.archie.rules;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

/**
 * Created by pieter.bos on 27/10/15.
 */
public enum OperatorKind {
    @JsonProperty(value="op_eq")
    @JsonAlias("eq")
    eq("op_eq", "="),
    @JsonProperty(value="op_ne")
    @JsonAlias("ne")
    ne("op_ne", "!=", "≠"),
    @JsonProperty(value="op_le")
    @JsonAlias("le")
    le("op_le", "<=", "≤"),
    @JsonProperty(value="op_lt")
    @JsonAlias("lt")
    lt("op_lt", "<"),
    @JsonProperty(value="op_ge")
    @JsonAlias("ge")
    ge("op_ge", ">=", "≥"),
    @JsonProperty(value="op_gt")
    @JsonAlias("gt")
    gt("op_gt", ">"),
    @JsonProperty(value="op_matches")
    @JsonAlias("matches")
    matches("op_matches", "matches", "∈", "is_in"),
    @JsonProperty(value="op_not")
    @JsonAlias("not")
    not("op_not", "not", "!", "∼", "¬"),
    @JsonProperty(value="op_and")
    @JsonAlias("and")
    and("op_and", "and", "∧"),
    @JsonProperty(value="op_or")
    @JsonAlias("or")
    or("op_or", "or", "∨"),
    @JsonProperty(value="op_xor")
    @JsonAlias("xor")
    xor("op_xor", "xor", "⊻"),
    @JsonProperty(value="op_implies")
    @JsonAlias("implies")
    implies("op_implies", "implies", "⇒"),
    @JsonProperty(value="op_for_all")
    @JsonAlias("for_all")
    for_all("op_for_all", "for_all", "∀", "every"),
    @JsonProperty(value="op_exists")
    @JsonAlias("exists")
    exists("op_exists", "exists" ,"∃"),
    @JsonProperty(value="op_plus")
    @JsonAlias("plus")
    plus("op_plus", "+"),
    @JsonProperty(value="op_minus")
    @JsonAlias("minus")
    minus("op_minus", "-"),
    @JsonProperty(value="op_multiply")
    @JsonAlias("multiply")
    multiply("op_multiply", "*"),
    @JsonProperty(value="op_divide")
    @JsonAlias("divide")
    divide("op_divide", "/"),
    @JsonProperty(value="op_modulo")
    @JsonAlias("modulo")
    modulo("op_modulo", "%"),
    @JsonProperty(value="op_exponent")
    @JsonAlias("exponent")
    exponent("op_exponent", "^");


    private final String identifier;
    private final ImmutableSet<String> codes;

    OperatorKind(String identifier, String... items) {
        this.identifier = identifier;
        codes = ImmutableSet.copyOf(items);
    }

    public String getDefaultCode() {
        return codes.iterator().next();
    }

    public static OperatorKind parseFromIdentifier(String identifier) {
        for(OperatorKind operator:values()) { //TODO: a hash implementation might be faster
            if(operator.name().equals(identifier)) {
                return operator;
            } else if (operator.getIdentifier().equals(identifier)) {
                return operator;
            }
        }
        return null;
    }

    public static OperatorKind parse(String operatorString) {
        operatorString = operatorString.toLowerCase();
        for(OperatorKind operator:values()) { //TODO: a hash implementation might be faster
            if(operator.codes.contains(operatorString)) {
                return operator;
            }
        }
        return null;
    }

    public String getIdentifier() {
        return identifier;
    }
}
