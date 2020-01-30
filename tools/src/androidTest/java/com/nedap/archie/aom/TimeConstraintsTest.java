package com.nedap.archie.aom;

import com.nedap.archie.aom.primitives.CTime;
import com.nedap.archie.base.Interval;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 23/02/16.
 */
public class TimeConstraintsTest {

    @Test
    public void timeConstraints() {
        CTime unconstrained = new CTime();
        unconstrained.isValidValue(LocalTime.now());
        unconstrained.isValidValue(LocalTime.of(12, 0));

        CTime interval = new CTime();
        interval.addConstraint(new Interval<>(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        assertTrue(interval.isValidValue(LocalTime.of(11, 0)));
        assertTrue(interval.isValidValue(LocalTime.of(12, 0)));
        assertFalse(interval.isValidValue(LocalTime.of(10, 59)));
        assertFalse(interval.isValidValue(LocalTime.of(12, 1)));

        CTime twoInterVals = new CTime();
        twoInterVals.addConstraint(new Interval<>(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        twoInterVals.addConstraint(new Interval<>(LocalTime.of(10, 0), LocalTime.of(11, 0)));
        assertTrue(twoInterVals.isValidValue(LocalTime.of(11, 0)));
        assertTrue(twoInterVals.isValidValue(LocalTime.of(12, 0)));
        assertTrue(twoInterVals.isValidValue(LocalTime.of(10, 59)));
        assertFalse(twoInterVals.isValidValue(LocalTime.of(12, 1)));
    }

    //TODO: date patterns. also the implementation :)
}
