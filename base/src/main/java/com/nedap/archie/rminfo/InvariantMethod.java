package com.nedap.archie.rminfo;

import java.lang.reflect.Method;
/** A reference to an invariant method, with the method reference and the invariant annotation present */
public class InvariantMethod {

    private Method method;
    private Invariant annotation;

    public InvariantMethod(Method method, Invariant annotation) {
        this.method = method;
        this.annotation = annotation;
    }

    public Method getMethod() {
        return method;
    }

    public Invariant getAnnotation() {
        return annotation;
    }
}
