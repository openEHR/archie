package com.nedap.archie.rminfo;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SpecificMethodSelectorTest {

    private static class A {
        A getA() { return null; }
        A getAnotherA() { return null; }
        B getB() { return null; }
    }

    private static class B extends A {
        A getA() { return null; }
        B getB() { return null; }
        C getC() { return null; }
    }

    private static class C extends A {
        A getA() { return null; }
    }

    @Test
    public void testCorrect() throws NoSuchMethodException {
        Method methodAgetA = A.class.getDeclaredMethod("getA");
        Method methodBgetA = B.class.getDeclaredMethod("getA");
        Method methodBgetB = B.class.getDeclaredMethod("getB");

        SpecificMethodSelector selector = new SpecificMethodSelector();

        assertEquals(methodBgetA, selector.apply(methodAgetA, methodBgetA));
        assertEquals(methodBgetA, selector.apply(methodBgetA, methodAgetA));

        assertEquals(methodBgetB, selector.apply(methodBgetA, methodBgetB));
        assertEquals(methodBgetB, selector.apply(methodBgetB, methodBgetA));

        assertEquals(methodBgetB, selector.apply(methodAgetA, methodBgetB));
        assertEquals(methodBgetB, selector.apply(methodBgetB, methodAgetA));
    }

    @Test
    public void testSameMethod() throws NoSuchMethodException {
        Method methodAgetA = A.class.getDeclaredMethod("getA");
        Method methodAgetAnotherA = A.class.getDeclaredMethod("getAnotherA");

        SpecificMethodSelector selector = new SpecificMethodSelector();

        assertThrows(IllegalArgumentException.class, () -> selector.apply(methodAgetA, methodAgetA));
        assertThrows(IllegalArgumentException.class, () -> selector.apply(methodAgetA, methodAgetAnotherA));
    }

    @Test
    public void testIncompatibleDeclaringClass() throws NoSuchMethodException {
        Method methodBgetA = B.class.getDeclaredMethod("getA");
        Method methodCgetA = C.class.getDeclaredMethod("getA");

        SpecificMethodSelector selector = new SpecificMethodSelector();

        assertThrows(IllegalArgumentException.class, () -> selector.apply(methodBgetA, methodCgetA));
    }

    @Test
    public void testIncompatibleReturnType() throws NoSuchMethodException {
        Method methodBgetB = B.class.getDeclaredMethod("getB");
        Method methodBgetC = B.class.getDeclaredMethod("getC");

        SpecificMethodSelector selector = new SpecificMethodSelector();

        assertThrows(IllegalArgumentException.class, () -> selector.apply(methodBgetB, methodBgetC));
    }

    @Test
    public void testMixedSpecific() throws NoSuchMethodException {
        Method methodAgetB = A.class.getDeclaredMethod("getB");
        Method methodBgetA = B.class.getDeclaredMethod("getA");

        SpecificMethodSelector selector = new SpecificMethodSelector();

        assertThrows(IllegalArgumentException.class, () -> selector.apply(methodAgetB, methodBgetA));
        assertThrows(IllegalArgumentException.class, () -> selector.apply(methodBgetA, methodAgetB));
    }
}
