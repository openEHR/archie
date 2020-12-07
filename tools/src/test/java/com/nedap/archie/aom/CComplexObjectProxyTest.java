package com.nedap.archie.aom;

import org.junit.Test;

import static org.junit.Assert.*;

public class CComplexObjectProxyTest {

    @Test
    public void isLeaf() {
        CComplexObject cComplexObject = new CComplexObject();

        assertTrue(cComplexObject.isLeaf());
    }
}
