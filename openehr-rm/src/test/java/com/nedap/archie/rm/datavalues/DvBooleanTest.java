package com.nedap.archie.rm.datavalues;

import org.junit.Test;
import org.openehr.rm.datavalues.DvBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
