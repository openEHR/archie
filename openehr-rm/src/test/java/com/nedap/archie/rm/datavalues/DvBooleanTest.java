package com.nedap.archie.rm.datavalues;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DvBooleanTest {
    @Test
    public void testEquals() {
        DvBoolean dvBooleanOne = new DvBoolean(true);
        DvBoolean dvBooleanTwo = new DvBoolean(Boolean.TRUE);
        DvBoolean dvBooleanThree = new DvBoolean(false);

        assertEquals(dvBooleanOne, dvBooleanTwo);
        assertNotEquals(dvBooleanOne, dvBooleanThree);
    }
}
