package com.nedap.archie.rminfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines a method as being an invariant, which means it will be checked during validation
 * automatically. The method must be of a boolean return type, and not have any parameters
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Invariant {
    /** the name of the Invariant */
    String value();
}
