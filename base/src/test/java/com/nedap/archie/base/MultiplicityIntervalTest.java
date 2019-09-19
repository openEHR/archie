package com.nedap.archie.base;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MultiplicityIntervalTest {
    @Test
    public void testToString() {
        assertEquals("0..0", MultiplicityInterval.createProhibited().toString());
        assertEquals("3..*", MultiplicityInterval.createUpperUnbounded(3).toString());
        assertEquals("1..1", MultiplicityInterval.createMandatory().toString());
        assertEquals("0..1", MultiplicityInterval.createOptional().toString());
        assertEquals("0..*", MultiplicityInterval.unbounded().toString());
        assertEquals("0..*", MultiplicityInterval.createOpen().toString());
        assertEquals("2..4", MultiplicityInterval.createBounded(2, 4).toString());

        // These situations should not happen:
        assertEquals(">2..<4",new MultiplicityInterval(2, false, false, 4, false, false).toString());
        assertEquals("*..*",new MultiplicityInterval(null, false, true, null, false, true).toString());
    }
}
