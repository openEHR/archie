package com.nedap.archie.rminfo;

import org.junit.jupiter.api.Test;

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
        @Override
        A getA() { return null; }
        @Override
        B getB() { return null; }
        C getC() { return null; }
    }

    private static class C extends A {
        @Override
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

        Exception exception = assertThrows(IllegalArgumentException.class, () -> selector.apply(methodAgetA, methodAgetA));
        assertEquals("Similar methods com.nedap.archie.rminfo.SpecificMethodSelectorTest$A com.nedap.archie.rminfo.SpecificMethodSelectorTest$A.getA() and com.nedap.archie.rminfo.SpecificMethodSelectorTest$A com.nedap.archie.rminfo.SpecificMethodSelectorTest$A.getA()", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> selector.apply(methodAgetA, methodAgetAnotherA));
        assertEquals("Similar methods com.nedap.archie.rminfo.SpecificMethodSelectorTest$A com.nedap.archie.rminfo.SpecificMethodSelectorTest$A.getA() and com.nedap.archie.rminfo.SpecificMethodSelectorTest$A com.nedap.archie.rminfo.SpecificMethodSelectorTest$A.getAnotherA()", exception.getMessage());
    }

    @Test
    public void testIncompatibleDeclaringClass() throws NoSuchMethodException {
        Method methodBgetA = B.class.getDeclaredMethod("getA");
        Method methodCgetA = C.class.getDeclaredMethod("getA");

        SpecificMethodSelector selector = new SpecificMethodSelector();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> selector.apply(methodBgetA, methodCgetA));
        assertEquals("Incompatible methods com.nedap.archie.rminfo.SpecificMethodSelectorTest$A com.nedap.archie.rminfo.SpecificMethodSelectorTest$B.getA() and com.nedap.archie.rminfo.SpecificMethodSelectorTest$A com.nedap.archie.rminfo.SpecificMethodSelectorTest$C.getA()", exception.getMessage());
    }

    @Test
    public void testIncompatibleReturnType() throws NoSuchMethodException {
        Method methodBgetB = B.class.getDeclaredMethod("getB");
        Method methodBgetC = B.class.getDeclaredMethod("getC");

        SpecificMethodSelector selector = new SpecificMethodSelector();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> selector.apply(methodBgetB, methodBgetC));
        assertEquals("Incompatible methods com.nedap.archie.rminfo.SpecificMethodSelectorTest$B com.nedap.archie.rminfo.SpecificMethodSelectorTest$B.getB() and com.nedap.archie.rminfo.SpecificMethodSelectorTest$C com.nedap.archie.rminfo.SpecificMethodSelectorTest$B.getC()", exception.getMessage());
    }

    @Test
    public void testMixedSpecific() throws NoSuchMethodException {
        Method methodAgetB = A.class.getDeclaredMethod("getB");
        Method methodBgetA = B.class.getDeclaredMethod("getA");

        SpecificMethodSelector selector = new SpecificMethodSelector();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> selector.apply(methodAgetB, methodBgetA));
        assertEquals("Incompatible methods com.nedap.archie.rminfo.SpecificMethodSelectorTest$B com.nedap.archie.rminfo.SpecificMethodSelectorTest$A.getB() and com.nedap.archie.rminfo.SpecificMethodSelectorTest$A com.nedap.archie.rminfo.SpecificMethodSelectorTest$B.getA()", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> selector.apply(methodBgetA, methodAgetB));
        assertEquals("Incompatible methods com.nedap.archie.rminfo.SpecificMethodSelectorTest$A com.nedap.archie.rminfo.SpecificMethodSelectorTest$B.getA() and com.nedap.archie.rminfo.SpecificMethodSelectorTest$B com.nedap.archie.rminfo.SpecificMethodSelectorTest$A.getB()", exception.getMessage());
    }
}
