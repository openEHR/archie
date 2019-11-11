package com.nedap.archie.rminfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark methods as to be ignored by ReflectionModelInfoLookup, because they are not specified as a property in the model specification (eg openehr RM),
 * but just an extra method
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RMPropertyIgnore {
}
