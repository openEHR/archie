package com.nedap.archie.rules;

import java.util.Collection;

/**
 * Created by pieter.bos on 31/03/16.
 */
public enum PrimitiveType {
    Integer, Real, Boolean, String, Character, Date, Time, DateTime, Duration, Interval, List, Unknown, ObjectReference;//TODO: interval or list with generics?

    public static PrimitiveType fromJavaType(Class<?> clazz) {
        if(Collection.class.isAssignableFrom(clazz)) {
            return List;
        }
        if(Number.class.isAssignableFrom(clazz)) {
            if(clazz.equals(Long.class) || clazz.equals(Integer.class)) {
                return Integer;
            } else if (clazz.equals(Double.class)) {
                return Real;
            }
        }
        if(clazz.equals(Boolean.class)) {
            return Boolean;
        }
        if(CharSequence.class.isAssignableFrom(clazz)) {
            return String;
        }
        return Unknown;//or throw exception?

    }

    public static PrimitiveType fromExpressionType(ExpressionType type) {
        switch(type) {
            case STRING:
                return String;
            case BOOLEAN:
                return Boolean;
            case INTEGER:
                return Integer;
            case REAL:
                return Real;
            case DATE:
                return Date;
            case TIME:
                return Time;
            case DATETIME:
                return DateTime;
        }
        return null;
    }
}
