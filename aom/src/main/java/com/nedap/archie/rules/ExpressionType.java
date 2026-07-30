package com.nedap.archie.rules;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * TODO: this should contain all primitive types and primitive types should be merged into this
 * Created by pieter.bos on 27/10/15.
 */
public enum ExpressionType {
     BOOLEAN, STRING, INTEGER, REAL, DATE, TIME, DATETIME, DURATION, C_STRING;

    @JsonCreator
    public static ExpressionType fromString(String string) {
        switch(string) {
            case "Boolean":
                return BOOLEAN;
            case "String":
                return STRING;
            case "Integer":
                return INTEGER;
            case "Real":
                return REAL;
            case "Date":
                return DATE;
            case "Time":
                return TIME;
            case "DateTime":
                return DATETIME;
            case "Duration":
                return DURATION;
            case "CString":
                return C_STRING;
        }
        // Fall back to case-insensitive match against enum constant names (e.g. "C_STRING")
        for (ExpressionType type : values()) {
            if (type.name().equalsIgnoreCase(string)) {
                return type;
            }
        }
        return null;
    }

    public String toString() {
        return switch (this) {
            case BOOLEAN -> "Boolean";
            case STRING -> "String";
            case INTEGER -> "Integer";
            case REAL -> "Real";
            case DATE -> "Date";
            case TIME -> "Time";
            case DATETIME -> "DateTime";
            case DURATION -> "Duration";
            case C_STRING -> "CString";
        };
    }
}
