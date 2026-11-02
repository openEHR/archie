package com.nedap.archie.query;

import org.antlr.v4.runtime.InputMismatchException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class APathQueryTest {

    @Test
    public void validPaths() {
        assertDoesNotThrow(() -> new APathQuery("/items[at0001]"));
        assertDoesNotThrow(() -> new APathQuery("/value"));
        assertDoesNotThrow(() -> new APathQuery("/items[at0001]/value"));
        assertDoesNotThrow(() -> new APathQuery("/[at0001]"));
    }

    @Test
    public void nullRelativeLocationPathContext() {
        Throwable t = assertIllegalPath("[");
        assertEquals("invalid relative path expression: [", t.getMessage());
        assertEquals("[", ((InputMismatchException) t.getCause()).getOffendingToken().getText());
    }

    @Test
    public void nullStepNodeTest() {
        Throwable t1 = assertIllegalPath("/2");
        assertEquals("invalid relative path expression: /2", t1.getMessage());
        assertEquals("2", ((InputMismatchException) t1.getCause()).getOffendingToken().getText());

        Throwable t2 = assertIllegalPath("/value/");
        assertEquals("invalid relative path expression: /value/", t2.getMessage());
        assertEquals("<EOF>", ((InputMismatchException) t2.getCause()).getOffendingToken().getText());
    }

    @Test
    public void invalidSlashes() {
        Throwable t = assertIllegalPath(" /");
        assertEquals("invalid relative path expression:  /", t.getMessage());
        assertEquals("<EOF>", ((InputMismatchException) t.getCause()).getOffendingToken().getText());

        assertThrows(UnsupportedOperationException.class, () -> new APathQuery("/slash//"));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static Throwable assertIllegalPath(String query) {
        return assertThrows(IllegalArgumentException.class, () -> new APathQuery(query));
    }
}
