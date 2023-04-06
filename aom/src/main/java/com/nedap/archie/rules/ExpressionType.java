package com.nedap.archie.rules;

/**
 * TODO: this should contain all primitive types and primitive types should be merged into this
 * Created by pieter.bos on 27/10/15.
 */
public enum ExpressionType {
     BOOLEAN, STRING, INTEGER, REAL, DATE, TIME, DATETIME;

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
        }
        return null;
    }

    public String toString() {
        switch(this) {
            case BOOLEAN:
                return "Boolean";
            case STRING:
                return "String";
            case INTEGER:
                return "Integer";
            case REAL:
                return "Real";
            case DATE:
                return "Date";
            case TIME:
                return "Time";
            case DATETIME:
                return "DateTime";
        }
        return null;
    }
}
