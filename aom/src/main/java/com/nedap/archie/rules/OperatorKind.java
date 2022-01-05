package com.nedap.archie.rules;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Created by pieter.bos on 27/10/15.
 */
public enum OperatorKind {
    @JsonProperty(value="op_eq")
    @JsonAlias("eq")
    eq("="), ne("!=", "≠"),
    @JsonProperty(value="op_le")
    @JsonAlias("le")
    le("<=", "≤"), lt("<"),
    @JsonProperty(value="op_ge")
    @JsonAlias("ge")
    ge(">=", "≥"), gt(">"),
    @JsonProperty(value="op_matches")
    @JsonAlias("matches")
    matches("matches", "∈", "is_in"),
    @JsonProperty(value="op_not")
    @JsonAlias("not")
    not("not", "!", "∼", "¬"),
    @JsonProperty(value="op_and")
    @JsonAlias("and")
    and("and", "∧"),
    @JsonProperty(value="op_or")
    @JsonAlias("or")
    or("or", "∨"),
    @JsonProperty(value="op_xor")
    @JsonAlias("xor")
    xor("xor", "⊻"),
    @JsonProperty(value="op_implies")
    @JsonAlias("implies")
    implies("implies", "⇒"),
    @JsonProperty(value="op_for_all")
    @JsonAlias("for_all")
    for_all("for_all", "∀", "every"),
    @JsonProperty(value="op_exists")
    @JsonAlias("exists")
    exists("exists" ,"∃"),
    @JsonProperty(value="op_plus")
    @JsonAlias("plus")
    plus("+"),
    @JsonProperty(value="op_minus")
    @JsonAlias("minus")
    minus("-"),
    @JsonProperty(value="op_multiply")
    @JsonAlias("multiply")
    multiply("*"),
    @JsonProperty(value="op_divide")
    @JsonAlias("divide")
    divide("/"),
    @JsonProperty(value="op_modulo")
    @JsonAlias("modulo")
    modulo("%"),
    @JsonProperty(value="op_exponent")
    @JsonAlias("exponent")
    exponent("^");

    private ImmutableSet<String> codes;

    OperatorKind(String... items) {
        codes = ImmutableSet.copyOf(items);
    }

    public String getDefaultCode() {
        return codes.iterator().next();
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
        return codes.iterator().next();
    }

}
