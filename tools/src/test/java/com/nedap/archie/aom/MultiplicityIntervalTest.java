package com.nedap.archie.aom;

import com.nedap.archie.base.MultiplicityInterval;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by pieter.bos on 14/04/2017.
 */
public class MultiplicityIntervalTest {

    @Test
    public void mandatory() {
        assertFalse(new MultiplicityInterval(0, 0).isMandatory());
        assertFalse(new MultiplicityInterval(0, 1).isMandatory());
        assertTrue(new MultiplicityInterval(1, 1).isMandatory());
        assertFalse(MultiplicityInterval.unbounded().isMandatory());
    }

    @Test
    public void open() {
        assertFalse(new MultiplicityInterval(0, 0).isOpen());
        assertFalse(new MultiplicityInterval(0, 1).isOpen());
        assertFalse(new MultiplicityInterval(1, 1).isOpen());
        assertTrue(MultiplicityInterval.unbounded().isOpen());
    }

    @Test
    public void prohibited() {
        assertTrue(new MultiplicityInterval(0, 0).isProhibited());
        assertFalse(new MultiplicityInterval(0, 1).isProhibited());
        assertFalse(new MultiplicityInterval(1, 1).isProhibited());
        assertFalse(MultiplicityInterval.unbounded().isProhibited());
    }

    @Test
    public void optional() {
        assertFalse(new MultiplicityInterval(0, 0).isOptional());
        assertTrue(new MultiplicityInterval(0, 1).isOptional());
        assertFalse(new MultiplicityInterval(1, 1).isOptional());
        assertFalse(MultiplicityInterval.unbounded().isOptional());
    }
}
