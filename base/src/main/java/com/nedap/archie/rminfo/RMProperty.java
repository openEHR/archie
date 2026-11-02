package com.nedap.archie.rminfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to override name of RM property.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RMProperty {
    /**
     * The name of the RM Property
     * @return the name of the RM Property
     */
    String value();

    /**
     * Whether this property is Computed, an actual in memory property or whether this should be autodetected.
     * Defaults to auto-detected, which usually is correct
     * @return whether this property is computed, an actual property or this should be autodetected
     */
    PropertyType computed() default PropertyType.AUTO_DETECT;
}
