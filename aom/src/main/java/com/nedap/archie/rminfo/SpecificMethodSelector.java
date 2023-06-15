package com.nedap.archie.rminfo;

import java.lang.reflect.Method;
import java.util.function.BinaryOperator;

/**
 * Selector which returns the most specific method of two methods.
 *
 * This will return the method with the most specific declaring class and return type.
 *
 * This will throw a IllegalArgumentException if the methods are incompatible or similar.
 */
class SpecificMethodSelector implements BinaryOperator<Method> {
    public Method apply(Method method1, Method method2) {
        Class<?> class1 = method1.getDeclaringClass();
        Class<?> class2 = method2.getDeclaringClass();

        Class<?> returnType1 = method1.getReturnType();
        Class<?> returnType2 = method2.getReturnType();

        if (class1 == class2 && returnType1 == returnType2) {
            throw new IllegalArgumentException("Similar methods " + method1.toGenericString() + " and " + method2.toGenericString());
        } else if (class1.isAssignableFrom(class2) && returnType1.isAssignableFrom(returnType2)) {
            return method2;
        } else if (class2.isAssignableFrom(class1) && returnType2.isAssignableFrom(returnType1)) {
            return method1;
        } else {
            // Methods are incompatible
            throw new IllegalArgumentException("Incompatible methods " + method1.toGenericString() + " and " + method2.toGenericString());
        }
    }
}
