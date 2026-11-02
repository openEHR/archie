package com.nedap.archie.aom.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AOMUtilsTest {

    @Test
    public void codeAtLevel() {
        assertEquals("id1", AOMUtils.codeAtLevel("id1", 0));
        assertEquals("id1", AOMUtils.codeAtLevel("id1.1", 0));
        assertEquals("id1.1", AOMUtils.codeAtLevel("id1.1", 1));
        assertEquals("id1.1", AOMUtils.codeAtLevel("id1.1.1", 1));
        assertEquals("id1", AOMUtils.codeAtLevel("id1.0.1", 1));
    }

}
