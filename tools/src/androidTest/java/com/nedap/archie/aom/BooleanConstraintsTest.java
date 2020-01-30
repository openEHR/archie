package com.nedap.archie.aom;

import com.nedap.archie.aom.primitives.CBoolean;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 23/02/16.
 */
public class BooleanConstraintsTest {

    @Test
    public void onlyFalse() {
        CBoolean test = new CBoolean();
        test.addConstraint(false);
        assertTrue(test.isValidValue(false));
        assertFalse(test.isValidValue(true));
    }

    @Test
    public void onlyTrue() {
        CBoolean test = new CBoolean();
        test.addConstraint(true);
        assertTrue(test.isValidValue(true));
        assertFalse(test.isValidValue(false));
    }

    @Test
    public void both() {
        CBoolean test = new CBoolean();
        test.addConstraint(true);
        test.addConstraint(false);
        assertTrue(test.isValidValue(true));
        assertTrue(test.isValidValue(false));

        CBoolean test2 = new CBoolean();
        assertTrue(test2.isValidValue(true));
        assertTrue(test2.isValidValue(false));
    }
}
