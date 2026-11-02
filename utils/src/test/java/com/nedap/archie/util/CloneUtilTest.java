package com.nedap.archie.util;

import com.nedap.archie.base.Cardinality;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void cloneUri() {
        URI original = URI.create("https://example.com/image.jpg");
        URI clone = CloneUtil.clone(original);
        assertNotNull(clone);
        assertEquals("https://example.com/image.jpg", clone.toString());
    }
}
