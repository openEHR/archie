package com.nedap.archie.rminfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Naming strategy for models. Default implementation exists for the Archie Reference Model implementation.
 *
 * Other implementations can be made for other reference model implementations.
 *
 * Created by pieter.bos on 26/03/16.
 */
public interface ModelNamingStrategy {

    String getAttributeName(Field field, Method method);
    String getTypeName(Class<?> clazz);

}
