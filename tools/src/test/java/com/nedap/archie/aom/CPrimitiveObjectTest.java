package com.nedap.archie.aom;

import com.nedap.archie.aom.primitives.CBoolean;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class CPrimitiveObjectTest {

    @Test
    public void isLeaf() {
        CBoolean cBoolean = new CBoolean();

        assertTrue(cBoolean.isLeaf());
    }
}
