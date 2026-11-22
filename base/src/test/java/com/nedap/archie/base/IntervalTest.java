package com.nedap.archie.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntervalTest {

    @Test
    public void hasWithMissingLimitOnBoundedSide() {
        //built like a deserializer does (no-arg constructor + setters): the side without a limit keeps
        //unbounded = false. The missing limit must not make has() throw, and does not constrain the value.
        Interval<Integer> upperOnly = new Interval<>();
        upperOnly.setUpper(130);
        assertTrue(upperOnly.has(100));
        assertTrue(upperOnly.has(130));
        assertFalse(upperOnly.has(162));

        Interval<Integer> lowerOnly = new Interval<>();
        lowerOnly.setLower(10);
        assertTrue(lowerOnly.has(10));
        assertTrue(lowerOnly.has(1000));
        assertFalse(lowerOnly.has(9));
    }
}
