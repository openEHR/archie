package com.nedap.archie.rminfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Naming strategy for models. Default implementation exists for the Archie Reference Model implementation.
 *
 * Other implementations can be made for other reference model implementations.
 *
 * Created by pieter.bos on 26/03/16.
 */
public interface ModelNamingStrategy {

    /**
     * Returns the OpenEHR standards compliant attribute name of the attribute implemenetd by the given java method
     * and/or field.
     * Either field or method must be  non-null, or both. If they are both non-null, they must point to the implementation
     * of the same attribute in the specification
     * @param field the field reference, if present
     * @param method the method reference, if present.
     * @return the name
     */
    String getAttributeName(Field field, Method method);

    /**
     * Returns the Model-standard compliant name of the given class.
     * @param clazz the class to name
     * @return the name
     */
    String getTypeName(Class<?> clazz);

    /**
     * Return any alternative names for a given class, used for backwards compatibility with renamed classes
     * @param clazz the class to name
     * @return the list of alternative names, empty if no alternatives are present
     */
    List<String> getAlternativeTypeNames(Class<?> clazz);

    /**
     * Returns the Model-standard compliant name of an AOM CPrimitiveType class.
     * @param clazz the class to name
     * @return the name
     */
    String getTypeNameForCPrimitiveType(Class<?> clazz);

}
