package com.nedap.archie.aom;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class CComplexObjectProxyTest {

    @Test
    public void isLeaf() {
        CComplexObject cComplexObject = new CComplexObject();

        assertTrue(cComplexObject.isLeaf());
    }
}
