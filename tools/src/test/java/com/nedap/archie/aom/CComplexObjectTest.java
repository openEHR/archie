package com.nedap.archie.aom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CComplexObjectTest {

    @Test
    public void isLeafAttributes() {
        CComplexObject cComplexObject = new CComplexObject();

        assertTrue(cComplexObject.isLeaf());

        cComplexObject.addAttribute(new CAttribute("items"));

        assertFalse(cComplexObject.isLeaf());
    }

    @Test
    public void isLeafAttributeTuples() {
        CComplexObject cComplexObject = new CComplexObject();

        assertTrue(cComplexObject.isLeaf());

        cComplexObject.addAttributeTuple(new CAttributeTuple());

        assertFalse(cComplexObject.isLeaf());
    }
}