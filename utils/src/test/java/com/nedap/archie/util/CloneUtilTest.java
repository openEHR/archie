package com.nedap.archie.util;

import com.nedap.archie.base.Cardinality;
import org.junit.Test;

import static org.junit.Assert.*;

public class CloneUtilTest {
    @Test
    public void testClone() {
        Cardinality original = new Cardinality(1, 5);
        Cardinality clone = CloneUtil.clone(original);
        assertNotSame(original, clone);
        assertEquals(original, clone);
    }

    @Test
    public void testCloneNull() {
        Cardinality original = null;
        Cardinality clone = CloneUtil.clone(original);
        assertNull(clone);
    }
}
